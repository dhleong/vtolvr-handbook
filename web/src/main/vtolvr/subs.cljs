(ns vtolvr.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub :page :page)
(reg-sub :index :index)

; ======= home ============================================

(reg-sub
  :intro
  :<- [:index]
  (fn [index]
    (->> index :intro :contents)))

(reg-sub
  :sections
  :<- [:index]
  (fn [index]
    (->> index
         :sections)))
