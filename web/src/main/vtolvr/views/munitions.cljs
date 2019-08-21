(ns ^{:author "Daniel Leong"
      :doc "vtolvr.views.munitions"}
  vtolvr.views.munitions
  (:require [spade.core :refer [defattrs]]
            [vtolvr.forms :as forms]
            [vtolvr.styles :as styles :refer [flex theme]]
            [vtolvr.util :refer [<sub idify] :refer-macros [fn-click]]
            [vtolvr.views.widgets :refer [link] :refer-macros [icon]]
            [vtolvr.views.munitions.study :as study]))

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

(defattrs filters-style []
  (merge (flex :horz)
         {:padding-bottom "16px"})

  [:.title {:padding-right "8px"}])

(defattrs munitions-table-attrs []
  {:border-collapse 'collapse}

  [:tr:hover {:background-color "#222"}])

(defn- munitions-filters []
  (let [filters (<sub [:munitions/filter])]
    [:div.filters (filters-style)

     [:div.title "Filters:"]

     [:div.types
      [forms/select {:value (:type filters :all)
                     :>evt (fn [v] [:set-munition-filter :type v])}
       [:option {:key :all} "All Types"]
       (for [t (<sub [:munition-filters/type])]
         [:option {:key t} (str t)])]]
     ]))

(defn munitions-page []
  (let [{type-filter :type} (<sub [:munitions/filter])
        type-filtered? (not= :all (or type-filter :all))
        munitions (<sub [:munitions/filtered])]
    [:<>
     [munitions-filters]

     [:table (munitions-table-attrs)
      [:thead
       [:tr
        [:th "Name"]
        (when-not type-filtered?
          [:th "Type"])
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
          (when-not type-filtered?
            [:td (str (:type attrs))])
          [:td (str (:guidance attrs))]
          [:td (case (:fire-and-forget attrs)
                 nil "?"
                 true "Yes"
                 false "")]
          [:td (:cost attrs)]
          [:td (:mass attrs)]
          [:td (:radio-call attrs)]])]]]))

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
         :study [study/view])]] ]))
