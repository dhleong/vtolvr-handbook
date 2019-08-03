(ns vtolvr.fx
  (:require [clojure.core.async :refer [go-loop <!]]
            [re-frame.core :refer [reg-fx]]
            [vtolvr.util :refer [>evt]]
            [vtolvr.util.http :refer [GET]]
            [vtolvr.util.nav :as nav]))

(reg-fx
  :nav/replace!
  nav/replace!)


; ======= data fetching ===================================

(defn- data-url [language file]
  (str "/data/" (or language "en") "/" file))

(reg-fx
  :fetch/index
  (fn [language]
    (go-loop [[e result] (<! (GET (data-url language "index.edn")))]
      (>evt [:set-index (or e result)]))))
