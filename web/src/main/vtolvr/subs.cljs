(ns vtolvr.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub :page :page)
(reg-sub :index :index)
(reg-sub ::sections :sections)

; ======= home ============================================

(reg-sub
  :intro
  :<- [:index]
  (fn [index]
    (->> index :intro :contents)))

(reg-sub
  :index/sections
  :<- [:index]
  (fn [index]
    (->> index
         :sections)))


; ======= section view ====================================

(reg-sub
  :section
  :<- [:index/sections]
  :<- [::sections]
  (fn [[from-index loaded] [_ id]]
    (assoc-in
      (or (get loaded id)

          {:state :unloaded
           :section (get from-index id)})
      [:section :id]
      id)))
