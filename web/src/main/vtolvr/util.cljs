(ns vtolvr.util
  (:require [clojure.string :as str]
            [re-frame.core :refer [subscribe dispatch]]))

(def <sub (comp deref subscribe))
(def >evt dispatch)

(def is-safari?
  (memoize
    (fn is-safari? []
      (and (boolean js/navigator.vendor)
           (re-find #"Apple" js/navigator.vendor)))))

(defn idify [title]
  (-> title
      str/lower-case
      (str/replace #"[^a-z0-9]+" "-")

      ; strip leading and trailing dashes (could be more efficient, but eh)
      (str/replace #"^-*(.*?)-*$" "$1")))
