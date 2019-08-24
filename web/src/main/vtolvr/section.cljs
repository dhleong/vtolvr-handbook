(ns ^{:author "Daniel Leong"
      :doc "Section data processing"}
  vtolvr.section
  (:require [com.rpl.specter :as sp]
            [vtolvr.config :as config]
            [vtolvr.selectors :refer [hiccup-el]]))

(defn process-hiccup-img [attrs]
  (update attrs :src #(str config/site-prefix (subs % 1))))

(defn post-process [data]
  ; TODO post-process data for linkification, etc
  (->> data

       ; fix img tags to reference the correct path
       (sp/transform [sp/ALL (hiccup-el :img) 1]
                     process-hiccup-img)))
