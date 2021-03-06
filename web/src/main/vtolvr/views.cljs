(ns vtolvr.views
  (:require [vtolvr.styles :as styles]
            [vtolvr.util :refer [<sub]]
            [vtolvr.views.error-boundary :refer [error-boundary]]
            [vtolvr.views.home :as home]
            [vtolvr.views.munition-detail :as munitions]
            [vtolvr.views.section :as section]))

(def ^:private pages
  {:home #'home/view
   :munitions #'munitions/view
   :section #'section/view})

(defn loader []
  [:div (styles/loader) "Loading"])

(defn index-error [e]
  [:<>
   [:div "Error"]
   [:div (str e)]])

(defn main []
  (let [[page & args] (<sub [:page])
        index (<sub [:index])
        page-form (into [(get pages page)] args)]
    (println "[router]" page args page-form)

    [error-boundary
     (cond
       (map? index) page-form
       (= :loading index) [loader]
       :else [index-error index])]))

