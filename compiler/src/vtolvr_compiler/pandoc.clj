(ns ^{:author "Daniel Leong"
      :doc "Pandoc interactions"}
  vtolvr-compiler.pandoc
  (:require [clojure.java.shell :refer [sh]]
            [vtolvr-compiler.composite :refer [files->input-stream]])
  (:import (java.io InputStream ByteArrayInputStream SequenceInputStream)))

(def pandoc-opts
  {:from "markdown"
   :toc true
   :number-sections true

   ; causing weird indenting issue...
   ;; :top-level-division "part"

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

(defn from-stream [^InputStream stream]
  (let [{:keys [exit out err]} (apply sh
                                      (concat
                                        (->pandoc-invocation
                                          (assoc pandoc-opts
                                                 :output "test.pdf"))
                                        [:in stream]))]
    (if (= 0 exit)
      out
      (throw (RuntimeException.
               (str "Err (" exit ") running pandoc:\n" err))))))

(defn from-files
  "Given a sorted list of files, generate a PDF using pandoc"
  [files]
  (from-stream
    (files->input-stream files)))

