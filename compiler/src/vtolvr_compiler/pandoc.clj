(ns ^{:author "Daniel Leong"
      :doc "Pandoc interactions"}
  vtolvr-compiler.pandoc
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]])
  (:import (java.io InputStream ByteArrayInputStream SequenceInputStream)))

(def pandoc-invocation
  ["pandoc"
   "--from=markdown"
   "--toc"
   "--number-sections"
   "--top-level-division=part"
   "-"])

(defn from-stream [^InputStream stream]
  (let [{:keys [exit out err]} (apply sh
                                      (concat pandoc-invocation
                                              ["--output=test.pdf"]
                                              [:in stream]))]
    (if (= 0 exit)
      out
      (throw (RuntimeException.
               (str "Err (" exit ") running pandoc:\n" err))))))

(defn combine-input-streams
  "Given a sequence of markdown input streams, combine them into a single,
   coherent stream"
  [streams]
  (->> streams

       ; add some whitespace so headers don't break
       (interleave (repeatedly #(io/input-stream (.getBytes "\n"))))
       (java.util.Collections/enumeration)
       (SequenceInputStream.)))

(defn files->input-stream
  "Combine a sequence of markdown files into a single, coherent input stream"
  [files]
  (->> files
       (map io/input-stream)
       combine-input-streams))

(defn from-files
  "Given a sorted list of files, generate a PDF using pandoc"
  [files]
  (from-stream
    (files->input-stream files)))

