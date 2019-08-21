(ns ^{:author "Daniel Leong"
      :doc "Munitions section-specific parsing"}
  vtolvr-compiler.edn.munitions
  (:require [clojure.string :as str]
            [com.rpl.specter :as sp]
            [vtolvr-compiler.util :refer [deblank idify]]))

(defn- notes-path? [path]
  (some (fn [entry]
          (str/includes? entry "Employment"))
        path))

(defn- gather-munitions-paths
  ([section] (gather-munitions-paths {} section []))
  ([state section current-path]
   (let [children (some->> section
                           keys
                           (filter string?)
                           seq)]
     (if-not (or (notes-path? current-path)
                 children)
       ; this is a munition
       (update state :munitions conj current-path)

       (let [state (if (:contents section)
                     (update state :notes conj current-path)
                     state)]
         (reduce
           (fn [state k]
             (gather-munitions-paths state
                                     (get section k)
                                     (conj current-path k)))
           state
           children))))))

(defn- hiccup-el [el]
  (sp/recursive-path
    [] p
    (sp/cond-path
      (fn [v]
        (and (vector? v)
             (= el (first v))))
      sp/STAY

      vector?
      [sp/ALL p]

      :else sp/STOP)))

(defn- table-with-attrs [v]
  (and (vector? v)
       (= :table (first v))
       (some #{"Attribute"} (->> v
                                 (sp/select [(hiccup-el :th) 2])
                                 (map str/trim)))))

(defmulti process-attr-val (fn [attr _] attr))
(defmethod process-attr-val :default [_ v] v)

(defmethod process-attr-val :fire-and-forget [_ v]
  (when-not (= "?" v) (str/includes? v "Yes")))

(defmethod process-attr-val :guidance [_ v]
  (and (string? v)
       (some->> (str/split v #"[ ]+")
                (keep deblank)
                (keep idify)
                seq
                vec)))

(defmethod process-attr-val :type [_ v]
  (when v
    (keyword v)))

(defn extract-attrs [section]
  (some->> section :contents
           ; get all the :td elements from an Attribute table
           (sp/select [sp/ALL table-with-attrs (hiccup-el :td) 2])
           (partition 2)
           (reduce (fn [m [k v]]
                     (if-let [id (idify k)]
                       (assoc m id (process-attr-val id v))
                       m))
                   {})))

(defn post-process [section]
  (let [{:keys [munitions notes]} (gather-munitions-paths section)]
    {:title (:title section)

     :munitions (->> munitions
                     (map (fn [path]
                            (let [sub-section (get-in section path)]
                              {:name (last path)
                               :tags (butlast path)
                               :attrs (extract-attrs sub-section)
                               :contents (:contents sub-section)})))
                     (sort-by :name))

     :notes (->> notes
                 (map (fn [path]
                         {:path path
                          :contents (:contents (get-in section path))}))
                 (sort-by :path))}))
