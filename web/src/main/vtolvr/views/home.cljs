(ns vtolvr.views.home
  (:require [vtolvr.util :refer [<sub]]
            [vtolvr.views.widgets :refer [link]]))

(defn view []
  (let [intro (<sub [:intro])
        sections (<sub [:index/sections])]
    [:div.home
     [:div.intro intro]

     [:div
      [:b "Available sections:"]
      [:ul
       (for [[id {title :title}] sections]
         ^{:key id}
         [:li
          [link {:href (str "section/" (name id))} title]])]]
     ]))

