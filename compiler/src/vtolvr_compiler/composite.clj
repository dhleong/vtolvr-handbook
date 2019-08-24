(ns ^{:author "Daniel Leong"
      :doc "Compositing MD files"}
  vtolvr-compiler.composite
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io File InputStream ByteArrayInputStream SequenceInputStream)
           (java.util Collections)))

(defn combine-input-streams
  "Given a sequence of markdown input streams, combine them into a single,
   coherent stream"
  [streams]
  (->> streams

       ; add some whitespace so headers don't break
       (interleave (repeatedly #(io/input-stream (.getBytes "\n"))))
       (Collections/enumeration)
       (SequenceInputStream.)))

(defmulti ->markdown-stream type)
(defmethod ->markdown-stream File [^File o]
  ; files can directly become input streams
  (let [contents (slurp o)]
    (-> contents
        (str/replace #"!\[([^]]*)\]\(([^)]+)\)"
                     (fn [[_ caption raw-url]]
                       (let [full-url (io/file (.getParentFile o) raw-url)
                             resolved-url (.getAbsolutePath full-url)]
                         (str "![" caption "](" resolved-url ")"))))
        (.getBytes))))
(defmethod ->markdown-stream String [^String o]
  (.getBytes o))

(defn files->input-stream
  "Combine a sequence of markdown objects into a single, coherent input stream"
  [files]
  (->> files
       (map (comp io/input-stream ->markdown-stream))
       combine-input-streams))

