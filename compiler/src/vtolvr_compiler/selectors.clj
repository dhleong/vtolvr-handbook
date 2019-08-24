(ns ^{:author "Daniel Leong"
      :doc "Specter selectors"}
  vtolvr-compiler.selectors
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

      :else sp/STOP)))
