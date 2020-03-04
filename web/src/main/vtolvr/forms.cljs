(ns ^{:author "Daniel Leong"
      :doc "A simple, mostly-drop-in forms system"}
  vtolvr.forms
  (:require [vtolvr.util :refer [>evt <sub]]))

(defn- flatten-sequences [s]
  (mapcat (fn [sub]
            (if (vector? sub)
              [sub]
              sub))
          s))

(defn- key-from-children [children target-value]
  (some->> children
           flatten-sequences
           (some (fn [[_ attrs v]]
                   (when (= target-value v)
                     (:key attrs))))))

(defn- value-from-children [children target-key]
  (some->> children
           flatten-sequences
           (some (fn [[_ {child-key :key} v]]
                   (when (= target-key child-key)
                     v)))))

(defn- dispatch-change [opts new-value]
  (if-let [evt-builder (:>evt opts)]
    (>evt (evt-builder new-value))
    ((:on-change opts) new-value)))

(defn- current-value-key [opts]
  (or (when (contains? opts :value)
        (:value opts (:default opts)))

      (when-let [sub-form (:<sub opts)]
        (or (<sub sub-form) (:default opts)))

      (throw (ex-info "No current value provided to select" opts))))


; ======= <select> ========================================

(defn select
  "This <select> element operates on the :key of the option elements, rather
   than their string value. The nitty-gritty is abstracted away so you just
   set the appropriate `{:key}` attrs in the options, provide the key that
   should be selected in `:value` or a subscription vector in `:<sub`, and
   changes to the selected key will be dispatched via `:on-change`, or
   `:>evt` will be called to build an event vector to dispatch."
  [opts & children]
  (let [on-change (fn [e]
                    (let [new-value (.-value (.-target e))
                          new-key (key-from-children children new-value)]
                      (when-not new-key
                        (throw (ex-info (str "Unable to pick new key from value `" new-value "`")
                                        {:children children})))

                      (dispatch-change opts new-value)))

        value-key (current-value-key opts)]

    (into [:select (-> opts
                       (assoc :on-change on-change
                              :value (value-from-children children value-key))
                       (dissoc :selected :<sub :>evt))]

          children)))
