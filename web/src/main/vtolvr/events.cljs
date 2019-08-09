(ns vtolvr.events
  (:require [re-frame.core :refer [dispatch reg-event-db reg-event-fx
                                   path
                                   inject-cofx trim-v]]
            [vtolvr.db :as db]
            [vtolvr.section :as section]))

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

(reg-event-fx
  :get-section
  [trim-v]
  (fn [{:keys [db]} [section-id]]
    ; TODO language
    {:db (assoc-in db [:sections section-id :state] :loading)
     :fetch/section ["en" section-id]}))

(reg-event-db
  :set-section
  [trim-v]
  (fn [db [section-id data]]
    (assoc-in db [:sections section-id]
              (if (ex-message data)
                {:state :error
                 :error data}

                {:state :loaded
                 :section (section/post-process data)}))))


; ======= munitions =======================================

(reg-event-db
  :set-munition-filter
  [trim-v]
  (fn [db [filter-key filter-value]]
    (assoc-in db [:munitions/filter filter-key] filter-value)))
