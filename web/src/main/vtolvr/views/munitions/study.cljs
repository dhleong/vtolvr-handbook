(ns ^{:author "Daniel Leong"
      :doc "vtolvr.views.munitions.study"}
  vtolvr.views.munitions.study
  (:require [clojure.string :as str]
            [vtolvr.util :refer [<sub]]))

(defn view []
  (let [notes (<sub [:munitions/notes])]
    [:<>
     [:ul.toc
      (for [note notes]
        (when-let [path (seq (:path note))]
          ^{:key (:joined-path note)}
          [:li [:a {:href (str "#" (:joined-path note))
                    :data-pushy-ignore true}
                (str/join " > " path)]]))]

     (for [note notes]
       ^{:key (:path note)}
       [:div.note
        ; TODO
        (when-let [path (seq (:path note))]
          [:h4 {:id (:joined-path note)}
           (str/join " > " path)])
        [:div.body (:contents note)]])]))
