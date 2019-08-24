(ns ^{:author "Daniel Leong"
      :doc "vtolvr-compiler.watch"}
  vtolvr-compiler.watch
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [hawk.core :as hawk]
            [vtolvr-compiler.files :as files]))

(def watchable-extensions #{".md" ".svg"})

(defn start! [language on-change]
  (let [root (files/find-docs-dir)
        path (io/file root language)]
    (println "Watching " watchable-extensions " files on " (.getCanonicalPath path) " ...")
    (hawk/watch! [{:paths [path]
                   :filter (fn [_ {:keys [file]}]
                             (and (.isFile file)
                                  (some #(str/ends-with? (.getName file) %)
                                        watchable-extensions)))
                   :handler (fn [_ {:keys [file]}]
                              (println "CHANGED: " (.getCanonicalPath file))
                              (on-change))}])))
