(ns ^{:author "Daniel Leong"
      :doc "vtolvr.styles"}
  vtolvr.styles)

(defn flex [mode]
  {:display 'flex
   :flex-direction (case mode
                     :horz 'row
                     :vert 'column)})
