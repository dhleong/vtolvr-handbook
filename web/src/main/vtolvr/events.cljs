(ns vtolvr.events
  (:require [re-frame.core :refer [dispatch reg-event-db reg-event-fx
                                   path
                                   inject-cofx trim-v]]
            [vtolvr.db :as db]))

(reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :navigate!
  [trim-v]
  (fn [db page-spec]
    (assoc db :page page-spec)))


; ======= data-loading ====================================

(reg-event-fx
  ::get-index
  [trim-v]
  (fn [{:keys [db]} _]
    ; TODO language
    {:db (dissoc db :index)
     :fetch/index "en"}))

(reg-event-db
  :set-index
  [trim-v]
  (fn [db [data]]
    (assoc db :index data)))
