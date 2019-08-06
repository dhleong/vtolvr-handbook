(ns ^{:author "Daniel Leong"
      :doc "vtolvr.styles"}
  vtolvr.styles
  (:require [spade.core :refer [defglobal]]
            [garden.color :refer [lighten]]))

(def theme
  {:text-primary "#f4f7ff"
   :text-link "#16DBA3"})

(defglobal global-styles
  [:body {:background-color "#000"
          :color (theme :text-primary)}]

  [:a {:color (theme :text-link)
       :text-decoration 'none}
   [:&:hover {:color (lighten (theme :text-link) 33)}]])

(defn flex [mode]
  {:display 'flex
   :flex-direction (case mode
                     :horz 'row
                     :vert 'column)})
