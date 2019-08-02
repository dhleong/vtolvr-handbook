(ns ^{:author "Daniel Leong"
      :doc "Doc file collection and ordering"}
  vtolvr-compiler.files
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io File)))

(defn- find-docs-dir []
  (loop [dir (io/file "../doc")]
    (if (.isDirectory dir)
      dir
      (if-let [p (.getParent dir)]
        (recur (io/file p "doc"))
        (throw (IllegalStateException.
                 (str "Couldn't find docs dir; started at: "
                      (.getAbsoluteFile (io/file "../doc")))))))))

(defn- is-index-file? [^File f]
  (= "index.md" (.getName f)))

(defn sorted-files-in
  ([^File dir] (sorted-files-in 0 dir))
  ([depth, ^File dir]
   (let [all-files (.listFiles dir)
         sub-dirs (->> all-files
                       (filter #(.isDirectory %))
                       (sort))
         files (filter #(.isFile %) all-files)
         index (->> files
                    (filter is-index-file?)
                    first)

         children (concat
                    (->> files
                         (filter (complement is-index-file?))
                         sort)

                    (flatten
                      (map (partial sorted-files-in (inc depth))
                           sub-dirs)))]
     (cond
       index
       (cons index children)

       ; no explicit index? generate a fake one
       (> depth 0)
       (cons (str (str/join "" (repeat depth "#"))
                  " "
                  (.getName dir))
             children)

       ; at 0 depth, don't generate an index
       :else
       children))))

(defn collect
  ([] (collect "en"))
  ([language]
   (let [root (find-docs-dir)]
     (sorted-files-in (io/file root language)))))
