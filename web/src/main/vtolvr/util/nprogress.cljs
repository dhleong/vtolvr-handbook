(ns ^{:author "Daniel Leong"
      :doc "nprogress wrapper"}
  vtolvr.util.nprogress
  (:require [vtolvr.styles :as styles]
            [spade.core :refer [defglobal]]
            ["nprogress" :as nprogress]))

(def bar-color (:text-link styles/theme))

(defglobal nprogress-bar
  ["#nprogress" {:pointer-events 'none} ; make clicks pass-through
   [:.bar {:background bar-color
           :position 'fixed
           :z-index 1031
           :top 0
           :left 0

           :width "100%"
           :height "2px"}]

   ; fancy blur effect:
   [:.peg {:display 'block
           :position 'absolute
           :right "0px"
           :width "100px"
           :height "100%"
           :box-shadow [[0 0 "10px" bar-color] [0 0 "5px" bar-color]]
           :opacity 1.0

           :-webkit-transform "rotate(3deg) translate(0px, -4px)"
           :-ms-transform "rotate(3deg) translate(0px, -4px)"
           :transform "rotate(3deg) translate(0px, -4px)"}]])

(def start nprogress/start)
(def stop nprogress/done)
