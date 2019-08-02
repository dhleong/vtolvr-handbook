(ns ^{:author "Daniel Leong"
      :doc "Main compiler entry point"}
  vtolvr-compiler.core
  (:require [clojure.java.io :as io]
            [vtolvr-compiler.edn :as edn]
            [vtolvr-compiler.files :as files]
            [vtolvr-compiler.pandoc :as pandoc]))

(def ^:private project-root (io/file "../")) ; FIXME

; TODO language switching?
(def ^:private edn-path (io/file project-root "web/static/data/en"))
(def ^:private pdf-path (io/file project-root "vtolvr-handbook-en.pdf"))

(defn generate-edn [files]
  (println "Generating EDN files")
  (edn/from-files files edn-path))

(defn generate-pdf [files]
  (println "Generating PDF")
  (pandoc/from-files files (.getPath pdf-path)))

(defn -main []
  (let [files (files/collect)]

    (println "Collected files:")
    (doseq [f files]
      (println " - " (.getPath f)))

    (doto files
      generate-pdf
      generate-edn)

    (println "Done!")

    ; TODO figure out why we don't exit cleanly...?
    (System/exit 0)))
