(ns ^{:author "Daniel Leong"
      :doc "vtolvr.views.munitions"}
  vtolvr.views.munitions
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [spade.core :refer [defattrs]]
            [vtolvr.styles :refer [flex theme]]
            [vtolvr.util :refer [<sub] :refer-macros [fn-click]]))

(defattrs munitions []
  [:.header (merge (flex :horz :center/perpendicular)
                   {:height "42pt"
                    :padding-left "12pt"})
   [:.title {:font-size "28pt"}]
   [:.menu (flex :horz)
    [:.button {:cursor 'pointer
               :padding "8px"}
     [:&.selected {:color (theme :text-primary)
                   :text-decoration 'underline
                   :cursor 'default}]]]]

  [:.content {:position 'absolute
              :overflow 'auto
              :top "42pt"
              :bottom 0
              :width "100vw"}
   [:.container {:padding "12px"}]])

(defn munitions-page []
  (let [munitions (<sub [:munitions/filtered])]
    [:table.munitions-table
     [:thead
      [:tr
       [:th "Name"]
       [:th "Type"]
       [:th "Guidance"]
       [:th "Fire-and-forget?"]
       [:th "Cost"]
       [:th "Mass"]
       [:th "Radio call"]]]

     [:tbody
      (for [{:keys [attrs] :as m} munitions]
        ^{:key (:name m)}
        [:tr
         [:td (:name m)]
         [:td (str (:type attrs))]
         [:td (str (:guidance attrs))]
         [:td (case (:fire-and-forget attrs)
                nil "?"
                true "Yes"
                false "")]
         [:td (:cost attrs)]
         [:td (:mass attrs)]
         [:td (:radio-call attrs "(none)")]])]]))

(defn notes-page []
  (let [notes (<sub [:munitions/notes])]
    [:<>
     [:ul.toc
      (for [note notes]
        (when-let [path (seq (:path note))]
          ^{:key (:joined-path note)}
          [:li [:a {:href (str "#" (:joined-path note))
                    :data-pushy-ignore true}
                (str/join " > " path)]]))]

     (for [note notes]
       ^{:key (:path note)}
       [:div.note
        ; TODO
        (when-let [path (seq (:path note))]
          [:h4 {:id (:joined-path note)}
           (str/join " > " path)])
        [:div.body (:contents note)]])]))

(defn- toggle-button [menu-atom menu-key label]
  [:a {:on-click (fn-click
                   (reset! menu-atom menu-key))}
   [:div.button {:class (when (= menu-key @menu-atom)
                          "selected")}
    label]])

(defn view [_]
  ; TODO some ideas here:
  ;  - filter by type
  ;  - filter by name search
  ;  - compare multiple
  ;  - Show notes related to type

  (r/with-let [menu (r/atom :munitions)]
    [:div.munitions (munitions)
     [:div.header

      [:div.title "Munitions"]

      [:div.menu
       [toggle-button menu :munitions "Browse"]
       [toggle-button menu :employment "Study"]]]

     [:div.content
      [:div.container
       (case @menu
         :munitions [munitions-page]
         :employment [notes-page])]] ]))
