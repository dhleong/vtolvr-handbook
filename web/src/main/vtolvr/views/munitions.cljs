(ns ^{:author "Daniel Leong"
      :doc "vtolvr.views.munitions"}
  vtolvr.views.munitions)

(defn view [{:keys [notes munitions]}]
  ; TODO some ideas here:
  ;  - filter by type
  ;  - filter by name search
  ;  - compare multiple
  ;  - Show notes related to type
  [:div.munitions
   (for [note notes]
     ^{:key (:path note)}
     [:div.note
      ; TODO
      (when-let [path (seq (:path note))]
        [:h4 (str path)])
      [:div.content (:contents note)]])

   (for [m munitions]
     ^{:key (:name m)}
     [:div.munition
      [:h4 (:name m)]
      [:div.contents (:contents m)]])
   ])
