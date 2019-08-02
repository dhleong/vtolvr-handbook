(ns ^{:author "Daniel Leong"
      :doc "EDN generation for website use"}
  vtolvr-compiler.edn
  (:require [clojure.string :as str]
            [endophile
             [core :as parse]
             #_[hiccup :refer [to-hiccup]]]
            [vtolvr-compiler.composite :refer [files->input-stream]])
  (:import (java.io File InputStream)))

(def ^:private markdown-extensions
  #{:tables})

(defn- parse-stream [^InputStream stream]
  (-> stream
      slurp
      (parse/mp {:extensions (->> markdown-extensions
                                  (reduce (fn [m k]
                                            (assoc m k true))
                                          {}))})
      parse/to-clj))

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
                     (str "Level went from " last-level " to " level))))))

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

(defn path->file [path]
  ; TODO
  (str/join "/" path))

(defn section->file-pair [{path :path :as section}]
  [(path->file path)

   ; TODO section contents
   {:title (:title section)
    }
   ])

(defn- build-index [all-sections]
  (reduce
    (fn [m section]
      (assoc m
             (:title section)
             (:path section)))
    {}
    all-sections))

(defn- lazy-sectionify
  ([headings] (lazy-sectionify [] headings))
  ([all-sections headings]
   (if-let [h (first headings)]

     ; process another section
     (let [last-heading (peek all-sections)
           section (sectionify last-heading h (second headings))]
       (cons (section->file-pair section)
             (lazy-seq
               (lazy-sectionify (conj all-sections section)
                                (drop 2 headings)))))

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
                       (if tag
                         (= \h (first (name tag)))
                         false)))
       lazy-sectionify))

(defn from-stream [^InputStream stream, _output-path]
  ; TODO
  (process-stream stream))

(defn from-files [files, ^File output-path]
  (from-stream (files->input-stream files) output-path))
