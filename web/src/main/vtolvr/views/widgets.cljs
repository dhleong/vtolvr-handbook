(ns ^{:author "Daniel Leong"
      :doc "vtolvr.views.widgets"}
  vtolvr.views.widgets
  (:require [vtolvr.util.nav :as nav]))

(defn link
  "Drop-in replacement for :a that inserts the # in links if necessary"
  [attrs & contents]
  (into [:a.link (update attrs :href nav/prefix)]
        contents))
