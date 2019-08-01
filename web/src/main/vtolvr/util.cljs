(ns vtolvr.util
  (:require [re-frame.core :refer [subscribe dispatch]]))

(def <sub (comp deref subscribe))
(def >evt dispatch)

(def is-safari?
  (memoize
    (fn is-safari? []
      (and (boolean js/navigator.vendor)
           (re-find #"Apple" js/navigator.vendor)))))


