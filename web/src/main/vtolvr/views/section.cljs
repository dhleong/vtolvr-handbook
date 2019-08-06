(ns vtolvr.views.section
  (:require [vtolvr.util :refer [>evt <sub idify]]
            [vtolvr.views.error-boundary :refer [error-boundary]]
            [vtolvr.views.munitions :as munitions]))

(def ^:private section-renderers
  {:munitions #'munitions/view})

(defn- recursive-toc [toc-entries]
  [:ul
   (for [{:keys [title id children]} toc-entries]
     ^{:key id}
     [:<>

      [:li [:a {:href (str "#" (name id))
                :data-pushy-ignore true}
            title]]

      (when (seq children)
        [recursive-toc children])])])

(defn content-toc []
  (let [toc (<sub [:section/toc])]
    [:div.toc
     [recursive-toc toc]]))

(defn content-renderer [section]
  [error-boundary
   (:contents section)

   (for [title (->> section
                    keys
                    (filter string?)
                    (sort))]
     (let [{level :level :as subsection} (get section title)]
       ^{:key title}
       [:<>
        [(keyword (str "h" (inc level)))
         {:id (idify title)}
         title]
        [content-renderer subsection]]))])

(defn- with-header [section & body]
  (into [:<>
         [:h1 (:title section)]]
        body))

(defn- render-section [section]
  (if-let [renderer (get section-renderers (:id section))]
    ; section-specific rendering (esp munitions)
    [renderer section]

    ; default renderer
    [with-header section
     [content-toc]
     [content-renderer section]]))

(defn loader [{:keys [state section error]}]
  (case state
    :loading [with-header section
              [:div.loading "Loading..."] ]
    :loaded [render-section section]
    :error (do
             ; try to reload?
             (>evt [:get-section (:id section)])
             [with-header section
              [:div.error "ERROR:" error]])
    :unloaded (do
                (>evt [:get-section (:id section)])
                [with-header section
                 [:div.loading "Loading..."]])))

(defn view [section-id]
  (if-let [info (<sub [:section/current])]
    [:div.section
     [loader info]]

    [:div "No such section: " section-id]))
