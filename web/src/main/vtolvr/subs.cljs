(ns vtolvr.subs
  (:require [re-frame.core :refer [reg-sub]]
            [vtolvr.util :refer [idify]]))

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

(defn section-by-id [from-index loaded id]
  (assoc-in
    (or (get loaded id)

        {:state :unloaded
         :section (get from-index id)})
    [:section :id]
    id))

(reg-sub
  :section/by-id
  :<- [:index/sections]
  :<- [::sections]
  (fn [[from-index loaded] [_ id]]
    (section-by-id from-index loaded id)))

(reg-sub
  :section/current
  :<- [:page]
  :<- [:index/sections]
  :<- [::sections]
  (fn [[page from-index loaded] _]
    (when (= :section (first page))
      (section-by-id from-index loaded (second page)))))

(defn- section->toc [section]
  (->> section
       keys
       (filter string?)
       (sort)
       (reduce
         (fn [toc title]
           (conj toc {:title title
                      :id (idify title)
                      :children (section->toc (get section title))}))
         [])))

(reg-sub
  :section/toc
  :<- [:section/current]
  (fn [section]
    (section->toc (:section section))))
