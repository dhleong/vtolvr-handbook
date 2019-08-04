(ns ^{:author "Daniel Leong"
      :doc "vtolvr.views.munitions"}
  vtolvr.views.munitions
  (:require [reagent.core :as r]
            [vtolvr.util :refer [<sub] :refer-macros [fn-click]]))

(defn munitions-page []
  (let [munitions (<sub [:munitions/filtered])]
    [:<>
     (for [m munitions]
       ^{:key (:name m)}
       [:div.munition
        [:h4 (:name m)]
        [:div.contents (:contents m)]]) ]))

(defn notes-page [notes]
  [:<>
   (for [note notes]
     ^{:key (:path note)}
     [:div.note
      ; TODO
      (when-let [path (seq (:path note))]
        [:h4 (str path)])
      [:div.content (:contents note)]])])

(defn view [{:keys [notes]}]
  ; TODO some ideas here:
  ;  - filter by type
  ;  - filter by name search
  ;  - compare multiple
  ;  - Show notes related to type

  (r/with-let [menu (r/atom :munitions)]
    [:div.munitions

     [:div.menu
      [:a {:on-click (fn-click
                       (reset! menu :munitions))}
       [:div.button
        "Munitions"]]

      [:a {:on-click (fn-click
                       (reset! menu :employment))}
       [:div.button
        "Employment"]]]

     (case @menu
       :munitions [munitions-page]
       :employment [notes-page notes]) ]))
