(ns ^{:author "Daniel Leong"
      :doc "Main compiler entry point"}
  vtolvr-compiler.core
  (:require [clojure.java.io :as io]
            [vtolvr-compiler.pandoc :as pandoc])
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

(defn sorted-files-in [^File dir]
  (let [all-files (.listFiles dir)
        sub-dirs (filter #(.isDirectory %) all-files)
        files (filter #(.isFile %) all-files)
        index (->> files
                   (filter is-index-file?)
                   first)

        children (concat
                   (->> files
                        (filter (complement is-index-file?))
                        sort)

                   (flatten
                     (map sorted-files-in sub-dirs)))

        sorted-files (if index
                       (cons index children)
                       children)]
    sorted-files))

(defn collect-files
  ([] (collect-files "en"))
  ([language]
   (let [root (find-docs-dir)]
     (sorted-files-in (io/file root language)))))

(defn -main []
  (let [files (collect-files)]

    (println "Generating PDF from: ")
    (doseq [f files]
      (println " - " (.getPath f)))

    (pandoc/from-files files)

    (println "Done!")

    ; TODO figure out why we don't exit cleanly...?
    (System/exit 0)))
