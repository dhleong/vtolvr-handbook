(ns ^{:author "Daniel Leong"
      :doc "Munitions section-specific parsing"}
  vtolvr-compiler.edn.munitions)

(defn- notes-path? [path]
  (some #{"Employment"} path))

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

(defn post-process [section]
  (let [{:keys [munitions notes]} (gather-munitions-paths section)]
    ; TODO further index munitions based on table data?
    {:title (:title section)

     :munitions (->> munitions
                     (map (fn [path]
                            {:name (last path)
                             :tags (butlast path)
                             :contents (:contents (get-in section path))}))
                     (sort-by :name))

     :notes (->> notes
                 (map (fn [path]
                         {:path path
                          :contents (:contents (get-in section path))}))
                 (sort-by :path))}))
