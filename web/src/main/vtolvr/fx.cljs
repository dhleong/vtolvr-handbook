(ns vtolvr.fx
  (:require [clojure.core.async :refer [go-loop <!]]
            [re-frame.core :refer [reg-fx]]
            [vtolvr.config :as config]
            [vtolvr.util :refer [>evt]]
            [vtolvr.util.http :refer [GET]]
            [vtolvr.util.nav :as nav]))

(reg-fx
  :nav/replace!
  nav/replace!)


; ======= data fetching ===================================

(defn- data-url [language file]
  (str config/site-prefix "/data/" (or language "en") "/" file))

(defn- fetch-url [url callback-event]
  (go-loop [[e result] (<! (GET url))]
    (>evt (conj callback-event (or e result)))))

(reg-fx
  :fetch/index
  (fn [language]
    (fetch-url (data-url language "index.edn")
               [:set-index])))

(reg-fx
  :fetch/section
  (fn [[language id]]
    (fetch-url (data-url language (str (name id) ".edn"))
               [:set-section id])))
