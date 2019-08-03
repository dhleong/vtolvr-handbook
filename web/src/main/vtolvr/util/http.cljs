(ns ^{:author "Daniel Leong"
      :doc "core.async http wrapper"}
  vtolvr.util.http
  (:require [clojure.core.async :refer [chan put! to-chan <! >!]]
            [ajax.core :as ajax]
            [ajax.edn :refer [edn-response-format]]))

(defn GET [url]
  (let [ch (chan 1)]
    (ajax/GET url
              {:handler (fn [raw]
                          (put! ch [nil raw]))
               :response-format (edn-response-format)
               :keywords? true
               :error-handler (fn [e]
                                (put! ch [e]))})
    ch))
