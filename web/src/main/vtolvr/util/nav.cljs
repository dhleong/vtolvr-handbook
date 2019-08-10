(ns vtolvr.util.nav
  (:require [clojure.string :as str]
            [reagent.dom :as reagent-dom]
            [re-frame.core :refer [dispatch-sync]]
            [secretary.core :as secretary]
            [goog.events :as gevents]
            [goog.history.EventType :as HistoryEventType]
            [pushy.core :as pushy]
            [vtolvr.config :as config]
            [vtolvr.util :refer [is-safari? >evt]])
  (:import goog.History))

(def ^:private pushy-supported? (pushy/supported?))

(def ^:private pushy-prefix config/site-prefix)
(def ^:private secretary-prefix (if pushy-supported?
                                  pushy-prefix
                                  "#"))

(defn init! []
  (secretary/set-config! :prefix secretary-prefix))

;; from secretary
(defn- uri-without-prefix [uri]
  (str/replace uri (re-pattern (str "^" secretary-prefix)) ""))
(defn- uri-with-leading-slash
  "Ensures that the uri has a leading slash"
  [uri]
  (if (= "/" (first uri))
    uri
    (str "/" uri)))

;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (if pushy-supported?
    ; fancy html5 navigation
    (let [history (pushy/pushy
                    secretary/dispatch!
                    (fn [x]
                      (let [[uri-path _query-string]
                            (str/split (uri-without-prefix x) #"\?")
                            uri-path (uri-with-leading-slash uri-path)]
                        (when (secretary/locate-route uri-path)
                          x))))]
      (pushy/start! history))

    ; #-based navigation
    (doto (History.)
      (gevents/listen
        HistoryEventType/NAVIGATE
        (fn [event]
          (secretary/dispatch! (.-token event))))
      (.setEnabled true))))

(defn prefix
  "Prefix a link as necessary for :href-based navigation to work"
  [raw-link]
  (if pushy-supported?
    (str pushy-prefix raw-link)
    (str "#" raw-link)))

(defn navigate!
  [& args]
  (let [evt (into [:navigate!] args)]
    (if (is-safari?)
      ; NOTE: on Safari we do some whacky shit to prevent awful flashes
      ;  when swiping back. hopefully there's a more efficient way
      ;  to do this, but for now... this works
      (do
        (dispatch-sync evt)
        (reagent-dom/force-update-all))

      ; When we don't have to worry about back-swipe, we can be more
      ;  relaxed about handling navigation
      (>evt evt))))

(defn replace!
  "Wrapper around js/window.location.replace"
  [new-location]
  (js/window.location.replace
    (prefix new-location)))

