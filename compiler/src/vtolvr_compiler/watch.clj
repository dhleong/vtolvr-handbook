(ns ^{:author "Daniel Leong"
      :doc "vtolvr-compiler.watch"}
  vtolvr-compiler.watch
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [hawk.core :as hawk]
            [vtolvr-compiler.files :as files]))

(defn start! [language on-change]
  (let [root (files/find-docs-dir)
        path (io/file root language)]
    (println "Watching " (.getCanonicalPath path) " ...")
    (hawk/watch! [{:paths [path]
                   :filter (fn [_ {:keys [file]}]
                             (and (.isFile file)
                                  (str/ends-with? (.getName file)
                                                  ".md")))
                   :handler (fn [_ {:keys [file]}]
                              (println "CHANGED: " (.getCanonicalPath file))
                              (on-change))}])))
