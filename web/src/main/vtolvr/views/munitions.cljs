(ns ^{:author "Daniel Leong"
      :doc "vtolvr.views.munitions"}
  vtolvr.views.munitions
  (:require [reagent.core :as r]
            [spade.core :refer [defattrs]]
            [vtolvr.styles :refer [flex theme]]
            [vtolvr.util :refer [<sub] :refer-macros [fn-click]]))

(defattrs munitions []
  [:.menu (flex :horz)]
  [:.button {:cursor 'pointer
             :padding "8px"}
   [:&.selected {:color (theme :text-primary)
                 :cursor 'default}]])

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

(defn- toggle-button [menu-atom menu-key label]
  [:a {:on-click (fn-click
                   (reset! menu-atom menu-key))}
   [:div.button {:class (when (= menu-key @menu-atom)
                          "selected")}
    label]])

(defn view [{:keys [notes]}]
  ; TODO some ideas here:
  ;  - filter by type
  ;  - filter by name search
  ;  - compare multiple
  ;  - Show notes related to type

  (r/with-let [menu (r/atom :munitions)]
    [:div.munitions (munitions)

     [:div.menu
      [toggle-button menu :munitions "Browse"]
      [toggle-button menu :employment "Study"]]

     (case @menu
       :munitions [munitions-page]
       :employment [notes-page notes]) ]))
