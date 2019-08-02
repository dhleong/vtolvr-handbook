(ns ^{:author "Daniel Leong"
      :doc "Pandoc interactions"}
  vtolvr-compiler.pandoc
  (:require [clojure.java.shell :refer [sh]]
            [vtolvr-compiler.composite :refer [files->input-stream]])
  (:import (java.io InputStream ByteArrayInputStream SequenceInputStream)))

(def pandoc-opts
  {:from "gfm"
   :toc true
   :number-sections true
   :standalone true

   ; add pagebreaks around chapters
   :top-level-division "chapter"

   ; using "oneside" prevents pandoc (latex) from changing text
   ; alignment each page (as you might want for print media)
   :variable {:classoption "oneside"}

   :metadata {:title "VTOLVR Handbook"
              :subtitle "Everything you need to know and more"}})

(defn- format-opts [[k v]]
  (let [key-str (str "--" (name k))]
    (cond
      (true? v) [key-str]

      (map? v) (map (fn [[sub-key sub-val]]
                      (str key-str "=" (name sub-key) ":" sub-val))
                    v)

      :else [(str key-str "=" v)])))

(defn ->pandoc-invocation [opts]
  (cons "pandoc"
        (concat (mapcat format-opts opts)
                ["-"])))

(defn- process-stream [^InputStream stream, extra-opts]
  (let [invocation (->pandoc-invocation
                     (merge pandoc-opts
                            extra-opts))
        {:keys [exit out err]} (apply sh (concat
                                           invocation
                                           [:in stream]))]
    (if (= 0 exit)
      (do
        (when (not (empty? err))
          (println "PANDOC WARNINGS:\n" err))

        out)

      (throw (RuntimeException.
               (str "Err (" exit ") running pandoc:\n" err))))))

(defn from-stream [^InputStream stream output-name]
  (process-stream stream {:output output-name}))

(defn from-files
  "Given a sorted list of files, generate a PDF using pandoc"
  [files output-name]
  (from-stream
    (files->input-stream files)
    output-name))

