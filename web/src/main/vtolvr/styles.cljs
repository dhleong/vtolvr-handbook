(ns ^{:author "Daniel Leong"
      :doc "vtolvr.styles"}
  vtolvr.styles
  (:require [spade.core :refer [defglobal defattrs]]
            [garden.color :refer [lighten]]))

(def theme
  {:text-primary "#f4f7ff"
   :text-link "#16DBA3"})

(defglobal global-styles
  [:body {:background-color "#000"
          :color (theme :text-primary)
          :padding 0
          :margin 0}]

  [:a {:color (theme :text-link)
       :text-decoration 'none}
   [:&:hover {:color (lighten (theme :text-link) 33)}]

   [:i.material-icons {:padding "8px"
                       :margin-right "8px"}]
   ])

(defn flex [mode & extra]
  (reduce
    (fn [spec arg]
      (case arg
        :center/perpendicular (assoc spec :align-items 'center)))

    {:display 'flex
     :flex-direction (case mode
                       :horz 'row
                       :vert 'column)}
    extra))

(def standard-header
  (merge (flex :horz :center/perpendicular)
         {:height "42pt"
          :padding-left "12pt"}))

(defattrs loader []
  {:padding "12px"})
