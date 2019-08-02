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

(defn combine-path [last-heading level section-title]
  (let [{last-level :level last-path :path} last-heading]
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
                          "\n old title: " (:title last-heading)
                          "\n new title: " section-title))))))

(defn- tag->level [header-tag]
  (-> header-tag
      name
      (subs 1)
      (Integer/parseInt)))

(defn- sectionify [last-heading heading contents]
  (let [{tag :tag section-title :content} (first heading)
        level (dec (tag->level tag))
        title (str/join section-title)]
    {:level level
     :title title
     :path (combine-path last-heading level title)
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

(defn section->file-pair [{:keys [path contents] :as section}]
  [(path->file path)

   {:title (:title section)
    :hiccup (->> contents
                 (map clj->hiccup)
                 (into [:div]))}])

(defn- build-index [all-sections]
  (reduce
    (fn [m section]
      (assoc m
             (:title section)
             (path->file (:path section))))
    {}
    all-sections))

(defn- lazy-sectionify
  ([headings] (lazy-sectionify [] headings))
  ([all-sections headings]
   (if-let [h (first headings)]

     ; process another section
     (let [last-heading (peek all-sections)
           contents (second headings)

           ; handle empty sections
           first-tag (-> contents first :tag)
           has-contents? (or (nil? first-tag)
                            (not (is-header? first-tag)))
           contents (if has-contents?
                      contents
                      [])
           consumed (if has-contents?
                      2
                      1) ; empty section

           section (sectionify last-heading h contents)]
       (cons (section->file-pair section)
             (lazy-seq
               (lazy-sectionify (conj all-sections section)
                                (drop consumed headings)))))

     ; no more; emit the index
     [["index.edn" (build-index all-sections)]])))

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
       lazy-sectionify))

(defn from-stream [^InputStream stream, output-path]
  (doseq [[path content] (process-stream stream)]
    (let [full-path (io/file output-path path)]
      (io/make-parents full-path)
      (spit full-path content))))

(defn from-files [files, ^File output-path]
  (from-stream (files->input-stream files) output-path))
