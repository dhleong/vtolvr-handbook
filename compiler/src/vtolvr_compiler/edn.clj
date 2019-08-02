(ns ^{:author "Daniel Leong"
      :doc "EDN generation for website use"}
  vtolvr-compiler.edn
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [endophile
             [core :as parse]
             [hiccup :as hiccup]]
            [vtolvr-compiler.composite :refer [files->input-stream]])
  (:import (java.io File InputStream)))

(def ^:private markdown-extensions
  #{:tables})

(defn- is-header? [tag]
  (and tag
       (= \h (first (name tag)))))

(defn- parse-stream [^InputStream stream]
  (-> stream
      slurp
      (parse/mp {:extensions (->> markdown-extensions
                                  (reduce (fn [m k]
                                            (assoc m k true))
                                          {}))})
      parse/to-clj))

(defn- clj->hiccup [clj-xml]
  ; HACKS!
  (#'hiccup/clj2hiccup clj-xml))

(defn combine-path [last-section level section-title]
  (let [{last-level :level last-path :path} last-section]
    (cond
      (= 0 level) [section-title]

      (= last-level level) (-> last-path
                               pop
                               (conj section-title))

      (= (inc last-level) level) (conj last-path section-title)
      (< level last-level) (conj
                             (subvec last-path
                                     0
                                     level)
                             section-title)

      :else (throw (IllegalArgumentException.
                     (str "Level went from " last-level " to " level
                          "\n old title: " (:title last-section)
                          "\n new title: " section-title))))))

(defn- tag->level [header-tag]
  (-> header-tag
      name
      (subs 1)
      (Integer/parseInt)))

(defn- sectionify [last-section heading contents]
  (let [{tag :tag section-title-parts :content} (first heading)
        level (dec (tag->level tag))
        title (str/join section-title-parts)]
    {:level level
     :title title
     :path (combine-path last-section level title)
     :contents contents}))

(defn- title->filename [title]
  (-> title
      (str/lower-case)
      (str/replace #"[^a-z0-9]+" "-")

      ; strip leading and trailing dashes (could be more efficient, but eh)
      (str/replace #"^-*(.*?)-*$" "$1")))

(defn path->file [path]
  (str (->> path
            (map title->filename)
            (str/join "/"))  ; not File/separator since it's an URL path
       ".edn"))

(defn- build-index [all-sections]
  (->> all-sections
       keys
       (reduce (fn [m title]
                 (assoc m title (path->file [title])))
               {})))

(defn- combine-partitions [partitions]
  (when-let [h (first partitions)]
    (let [contents (second partitions)

          ; handle empty sections
          first-tag (-> contents first :tag)
          has-contents? (or (nil? first-tag)
                            (not (is-header? first-tag)))

          contents (if has-contents?
                     contents
                     [])
          consumed (if has-contents?
                     2
                     1)]  ; empty section
      (cons {:header h
             :contents (->> contents
                            clj->hiccup
                            (into [:div]))}
            (lazy-seq
              (combine-partitions (drop consumed partitions)))))))

(defn- eager-sectionify [sections]
  (loop [state {}
         sections sections]
    (if-not (seq sections)
      ; DONE
      (dissoc state :last-section)

      (let [{:keys [last-section]} state
            {:keys [header contents]} (first sections)
            new-section (sectionify last-section header contents)
            new-state (-> state
                          (assoc :last-section new-section)
                          (assoc-in (:path new-section) new-section))]
        (recur new-state
               (next sections))))))

(defn- sections->file-pairs [sections-map]
  (->> sections-map

       (map (fn [[title section]]
              [(path->file [title]) section]))

       (cons ["index.edn" (build-index sections-map)])))

(defn process-stream
  "Given a stream combined markdown stream, produces a sequence of
   `[file, contents]` pairs, where `file` is a relative file path
   and contents is an edn-printable data structure to write there"
  [^InputStream stream]
  (->> stream
       parse-stream

       (remove #(or (= "" %)
                    (= :comment (:type %))))

       (partition-by (fn [{tag :tag}]
                       (when (is-header? tag)
                         tag)))
       combine-partitions

       eager-sectionify
       sections->file-pairs))

(defn from-stream [^InputStream stream, output-path]
  (doseq [[path content] (process-stream stream)]
    (let [full-path (io/file output-path path)]
      (io/make-parents full-path)
      (spit full-path content))))

(defn from-files [files, ^File output-path]
  (from-stream (files->input-stream files) output-path))