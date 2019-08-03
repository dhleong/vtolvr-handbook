(ns vtolvr.views
  (:require [vtolvr.util :refer [<sub]]
            [vtolvr.views.error-boundary :refer [error-boundary]]
            [vtolvr.views.home :as home]))

(def ^:private pages
  {:home #'home/view})

(defn loader []
  [:div "Loading"])

(defn index-error [e]
  [:<>
   [:div "Error"]
   [:div (str e)]])

(defn main []
  (let [[page args] (<sub [:page])
        index (<sub [:index])
        page-form [(get pages page) args]]
    (println "[router]" page args page-form)

    [error-boundary
     (cond
       (map? index) page-form
       (nil? index) [loader]
       :else [index-error index])]))

