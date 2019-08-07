(ns ^{:author "Daniel Leong"
      :doc "vtolvr-compiler.util"}
  vtolvr-compiler.util
  (:require [clojure.string :as str]))

(defn deblank [s]
  (when-not (str/blank? s)
    s))

(defn idify [title]
  (some-> title
          str/lower-case
          (str/replace #"[^a-z0-9]+" "-")

          ; strip leading and trailing dashes (could be more efficient, but eh)
          (str/replace #"^-*(.*?)-*$" "$1")

          deblank
          keyword))
