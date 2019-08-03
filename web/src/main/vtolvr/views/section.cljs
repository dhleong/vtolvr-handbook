(ns vtolvr.views.section
  (:require [vtolvr.util :refer [>evt <sub]]))

(defn- render-section [section]
  ; TODO section-specific rendering? (esp munitions)
  [:<>
   [:div "TODO" ]
   ; TODO there are nested sections....
   (:contents section)])

(defn loader [{:keys [state section error]}]
  (case state
    :loading [:div.loading "Loading..."]
    :loaded [render-section section]
    :error (do
             ; try to reload?
             (>evt [:get-section (:id section)])
             [:div.error "ERROR:" error])
    :unloaded (do
                (>evt [:get-section (:id section)])
                [:div.loading "Loading..."])))

(defn view [section-id]
  (if-let [{:keys [section] :as info} (<sub [:section section-id])]
    [:div.section
     [:h1 (:title section)]

     [loader info]]

    [:div "No such section: " section-id]))
