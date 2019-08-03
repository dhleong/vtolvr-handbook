(ns vtolvr.views.section
  (:require [vtolvr.util :refer [<sub]]))

(defn view [section-id]
  ; FIXME load content; show spinner
  (if-let [section (get (<sub [:sections]) section-id)]
    ; TODO
    [:div (str section)]

    [:div "No such section: " section-id]))
