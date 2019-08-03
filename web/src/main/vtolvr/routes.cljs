(ns vtolvr.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:require [secretary.core :as secretary]
            [vtolvr.util.nav :as nav :refer [navigate!]]))

(defn- def-routes []
  (secretary/reset-routes!)

  ;;
  ;; app routes declared here:

  (defroute "/" []
    (navigate! :home))

  (defroute "/section/:id" [id]
    (navigate! :section (keyword id))))

(defn app-routes []
  (nav/init!)

  (def-routes)

  (nav/hook-browser-navigation!))

