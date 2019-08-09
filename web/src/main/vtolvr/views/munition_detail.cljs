(ns ^{:author "Daniel Leong"
      :doc "vtolvr.views.munition-detail"}
  vtolvr.views.munition-detail
  (:require [spade.core :refer [defattrs]]
            [vtolvr.styles :as styles]
            [vtolvr.views.section :as section]
            [vtolvr.views.widgets :refer [link] :refer-macros [icon]]
            [vtolvr.util :refer [<sub]]))

(defattrs munition-detail-style []
  [:.header styles/standard-header
   [:.title {:font-size "28pt"}]]

  [:.content {:padding "12px"}])

(defn loaded-view [munition]
  [:div (munition-detail-style)
   [:div.header

    [link {:href "section/munitions"} (icon :arrow-back)]

    [:div.title (:name munition)]]

   [:div.content
    [section/content-renderer munition]]

   ; relevant usage information:
   (for [{path :path :as note} (<sub [:munitions/notes-for munition])]
     ^{:key path}
     [:div.content
      [(str "h" (count path)) (last path)]
      [section/content-renderer note]])
   ])

(defn view [munition-id]
  [section/loader (<sub [:section/by-id :munitions])
   (if-let [munition (<sub [:munitions/by-id munition-id])]
     [loaded-view munition]
     [:div.error
      "No such munition"])])
