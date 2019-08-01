(ns vtolvr.views
  (:require [vtolvr.util :refer [<sub]]
            [vtolvr.views.error-boundary :refer [error-boundary]]
            [vtolvr.views.home :as home]))

(def ^:private pages
  {:home #'home/view})

(defn main []
  (let [[page args] (<sub [:page])
        page-form [(get pages page) args]]
    (println "[router]" page args page-form)

    [error-boundary
     page-form]))

