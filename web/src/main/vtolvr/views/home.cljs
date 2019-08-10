(ns vtolvr.views.home
  (:require [spade.core :refer [defattrs]]
            [vtolvr.config :as config]
            [vtolvr.util :refer [<sub]]
            [vtolvr.views.widgets :refer [link]]))

(defattrs home-styles []
  {:padding "12px"}

  [:.pdf-links {:padding "8px"}]

  [:.section-links {:padding "8px"}])

(defn view []
  (let [intro (<sub [:intro])
        sections (<sub [:index/sections])]
    [:div.home (home-styles)
     [:div.intro intro]

     [:div.pdf-links
      [:a {:href (str config/site-prefix "vtolvr-handbook-en.pdf")
           :download true}
       "Download PDF"]]

     [:div.section-links
      [:b "Available sections:"]
      [:ul
       (for [[id {title :title}] sections]
         ^{:key id}
         [:li
          [link {:href (str "section/" (name id))} title]])]]
     ]))

