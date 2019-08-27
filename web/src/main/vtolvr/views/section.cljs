(ns vtolvr.views.section
  (:require [spade.core :refer [defattrs]]
            [vtolvr.styles :as styles]
            [vtolvr.util :refer [>evt <sub idify]]
            [vtolvr.views.error-boundary :refer [error-boundary]]
            [vtolvr.views.munitions :as munitions]
            [vtolvr.views.widgets :refer [link] :refer-macros [icon]]))

(def ^:private section-renderers
  {:munitions #'munitions/view})

(defattrs section-attrs []
  {:padding "12px"}

  [:.header styles/standard-header
   [:.title styles/standard-title]])

(defn- recursive-toc [toc-entries]
  [:ul
   (for [{:keys [title id children]} toc-entries]
     [:<> {:key id}

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
       [:<> {:key title}
        [(keyword (str "h" (inc level)))
         {:id (idify title)}
         title]
        [content-renderer subsection]]))])

(defn- with-header [section & body]
  (into [:div (section-attrs)
         [:div.header
          [link {:href ""} (icon :home)]
          [:div.title (:title section)]]]
        body))

(defn- render-section [section subsection-id]
  (if-let [renderer (get section-renderers (:id section))]
    ; section-specific rendering (esp munitions)
    [renderer section subsection-id]

    ; default renderer
    ; NOTE: subsection-id is currently not used with the default renderer
    [with-header section
     [content-toc]
     [content-renderer section]]))

(defn loader
  ([info] (loader info render-section))
  ([{:keys [state section error subsection-id]} render-section]
   (case state
     :loading [with-header section
               [:div (styles/loader) "Loading..."] ]
     :loaded (if (fn? render-section)
               [render-section section subsection-id]
               render-section)
     :error (do
              ; try to reload?
              (>evt [:get-section (:id section)])
              [with-header section
               [:div.error "ERROR:" error]])
     :unloaded (do
                 (>evt [:get-section (:id section)])
                 [with-header section
                  [:div.loading (styles/loader) "Loading..."]]))))

(defn view [section-id subsection-id]
  (if-let [info (<sub [:section/current])]
    [:div.section
     [loader (assoc info :subsection-id subsection-id)]]

    [:div "No such section: " section-id]))
