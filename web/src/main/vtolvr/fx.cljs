(ns vtolvr.fx
  (:require [re-frame.core :refer [reg-fx]]
            [vtolvr.util.nav :as nav]))

(reg-fx
  :nav/replace!
  nav/replace!)
