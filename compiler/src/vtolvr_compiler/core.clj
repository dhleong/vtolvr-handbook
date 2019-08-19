(ns ^{:author "Daniel Leong"
      :doc "Main compiler entry point"}
  vtolvr-compiler.core
  (:require [clojure.java.io :as io]
            [vtolvr-compiler.edn :as edn]
            [vtolvr-compiler.files :as files]
            [vtolvr-compiler.pandoc :as pandoc]
            [vtolvr-compiler.watch :as watch]))

(def ^:private project-root (io/file "../")) ; FIXME

; TODO language switching?
(def ^:private edn-path (io/file project-root "web/public/data/en"))
(def ^:private pdf-path (io/file project-root "web/public/vtolvr-handbook-en.pdf"))

(defn generate-edn [files]
  (println "Generating EDN files")
  (edn/from-files files edn-path))

(defn generate-pdf [files]
  (println "Generating PDF")
  (pandoc/from-files files (.getPath pdf-path)))

(defn build-with [generate-fn]
  (let [files (files/collect)]

    (println "Collected files:")
    (doseq [f files]
      (if (string? f)
        (println " - (string) " f)
        (println " - " (.getPath f))))

    (generate-fn files)

    (println "Done!")))

(defn generate-all []
  (build-with (fn [files]
                (doto files
                  generate-pdf
                  generate-edn))))

(defn -main [& args]
  (cond
    (= ["--watch"] args)
    (watch/start! "en" generate-all)

    (= ["data"] args)
    (build-with generate-edn)

    (= ["pdf"] args)
    (build-with generate-pdf)

    :else
    (do
      (generate-all)

      ; TODO figure out why we don't exit cleanly...?
      (System/exit 0))))
