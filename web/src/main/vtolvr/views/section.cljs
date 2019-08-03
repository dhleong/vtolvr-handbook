(ns vtolvr.views.section
  (:require [vtolvr.util :refer [>evt <sub]]
            [vtolvr.views.error-boundary :refer [error-boundary]]))

(def ^:private section-renderers {}) ; TODO

(defn content-renderer [section]
  [error-boundary
   (:contents section)

   (for [title (->> section
                    keys
                    (filter string?))]
     (let [{level :level :as subsection} (get section title)]
       ^{:key title}
       [:<>
        [(keyword (str "h" (inc level))) title]
        [content-renderer subsection]]))])

(defn- render-section [section]
  (if-let [renderer (get section-renderers (:id section))]
    ; section-specific rendering (esp munitions)
    [renderer section]

    ; default renderer
    ; TODO table of contents?
    [content-renderer section]))

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
