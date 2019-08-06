(ns vtolvr.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [vtolvr.events :as events]
            [vtolvr.routes :as routes]
            [vtolvr.views :as views]
            [vtolvr.fx]
            [vtolvr.styles]
            [vtolvr.subs]))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch [::events/get-index])
  (routes/app-routes)
  (mount-root))

