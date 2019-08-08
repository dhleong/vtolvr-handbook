(ns ^{:author "Daniel Leong"
      :doc "vtolvr.views.munitions"}
  vtolvr.views.munitions
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [spade.core :refer [defattrs]]
            [vtolvr.styles :as styles :refer [flex theme]]
            [vtolvr.util :refer [<sub idify] :refer-macros [fn-click]]
            [vtolvr.views.widgets :refer [link] :refer-macros [icon]]))

(defattrs munitions []
  [:.header styles/standard-header
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
      (for [{attrs :attrs n :name} munitions]
        ^{:key n}
        [:tr
         [:td [link {:href (str "munitions/" (idify n))} n]]
         [:td (str (:type attrs))]
         [:td (str (:guidance attrs))]
         [:td (case (:fire-and-forget attrs)
                nil "?"
                true "Yes"
                false "")]
         [:td (:cost attrs)]
         [:td (:mass attrs)]
         [:td (:radio-call attrs)]])]]))

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

(defn- toggle-button [current-page url menu-key label]
  [:a {:href url}
   [:div.button {:class (when (= menu-key current-page)
                          "selected")}
    label]])

(defn view [_ subsection-id]
  (let [subsection-id (or subsection-id :browse)]

    ; TODO some ideas here:
    ;  - filter by type
    ;  - filter by name search
    ;  - compare multiple
    ;  - Show notes related to type

    [:div.munitions (munitions)
     [:div.header

      [link {:href ""} (icon :home)]

      [:div.title "Munitions"]

      [:div.menu
       [toggle-button subsection-id "/section/munitions" :browse "Browse"]
       [toggle-button subsection-id "/section/munitions/study" :study "Study"]]]

     [:div.content
      [:div.container
       (case subsection-id
         :browse [munitions-page]
         :study [notes-page])]] ]))
