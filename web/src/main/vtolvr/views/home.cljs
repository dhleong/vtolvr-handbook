(ns vtolvr.views.home
  (:require [vtolvr.util :refer [<sub]]))

(defn view []
  (let [index (<sub [:index])]
    [:div "Hello world!"
     (str index)]))

