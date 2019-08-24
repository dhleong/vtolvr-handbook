(ns ^{:author "Daniel Leong"
      :doc "vtolvr.selectors"}
  vtolvr.selectors
  (:require [com.rpl.specter :as sp]))

(defn hiccup-el [el]
  (sp/recursive-path
    [] p
    (sp/cond-path
      (fn [v]
        (and (vector? v)
             (= el (first v))))
      sp/STAY

      vector?
      [sp/ALL p]

      map?
      [sp/MAP-VALS p]

      :else sp/STOP)))
