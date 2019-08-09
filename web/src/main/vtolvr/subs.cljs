(ns vtolvr.subs
  (:require [clojure.string :as str]
            [re-frame.core :refer [reg-sub]]
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


; ======= munitions section ===============================

(reg-sub :munitions/filter :munitions/filter)

(reg-sub
  ::munitions-section
  :<- [:section/by-id :munitions]
  (fn [{:keys [state section]}]
    (when (and (= :loaded state)
               (= :munitions (:id section)))
      section)))

(reg-sub
  :munitions/all
  :<- [::munitions-section]
  (fn [section]
    (:munitions section)))

(reg-sub
  :munitions/notes
  :<- [::munitions-section]
  (fn [section]
    (->> section
         :notes
         (map (fn [n]
                (assoc n :joined-path (str/lower-case (str/join "-" (:path n))))))
         (sort-by :joined-path))))

(defn filter-rejects? [filter-map m]
  (or
    (when-let [text (:text filter-map)]
      (when-not (empty? text)
        (str/includes? (:name m) text)))

    (when-let [by-type (:type filter-map)]
      (when-not (= :all by-type)
        (not= by-type (:type (:attrs m) :other))))))

(reg-sub
  :munitions/filtered
  :<- [:munitions/all]
  :<- [:munitions/filter]
  (fn [[munitions filter-map]]
    (->> munitions
         (remove (partial filter-rejects? filter-map)))))

(reg-sub
  :munition-filters/type
  :<- [:munitions/all]
  (fn [munitions]
    (concat
      (->> munitions
           (keep (comp :type :attrs))
           distinct
           sort)
      [:other])))



; ======= munition details ================================

(reg-sub
  :munitions/by-id
  :<- [:munitions/all]
  (fn [munitions [_ id]]
    (->> munitions
         (filter #(= id (keyword (idify (:name %)))))
         first)))

(reg-sub
  :munitions/notes-for
  :<- [:munitions/notes]
  (fn [all-notes [_ munition]]
    (let [{{munition-type :type guidance :guidance} :attrs} munition
          path-prefix (name munition-type)
          guidance-strings (->> guidance
                                (map name)
                                (into #{}))
          guidance-check (fn [path]
                           (some #(str/includes? path %)
                                 guidance-strings))]
      (->> all-notes
           (filter #(let [joined-path (:joined-path %)]
                      (and (str/starts-with? joined-path path-prefix)
                           (guidance-check joined-path))))))))
