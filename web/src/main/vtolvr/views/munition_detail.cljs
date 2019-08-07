(ns ^{:author "Daniel Leong"
      :doc "vtolvr.views.munition-detail"}
  vtolvr.views.munition-detail
  (:require [vtolvr.views.section :as section]
            [vtolvr.util :refer [<sub]]))

(defn loaded-view [munition]
  [:div
   [:h1 (:name munition)]

   [section/content-renderer munition]

   ; TODO relevant usage information
   ])

(defn view [munition-id]
  [section/loader (<sub [:section/by-id :munitions])
   (if-let [munition (<sub [:munitions/by-id munition-id])]
     [loaded-view munition]
     [:div.error
      "No such munition"])])
