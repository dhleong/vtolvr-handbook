(ns ^{:author "Daniel Leong"
      :doc "vtolvr.forms"}
  vtolvr.forms
  (:require [vtolvr.util :refer [>evt <sub]]))

(defn- flatten-sequences [s]
  (mapcat (fn [sub]
            (if (vector? sub)
              [sub]

              sub)) s))

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

(defn select [opts & children]
  (let [on-change (fn [e]
                    (let [new-value (.-value (.-target e))
                          new-key (key-from-children children new-value)]
                      (when-not new-key
                        (throw (ex-info (str "Unable to pick new key from value `" new-value "`")
                                        {:children children})))

                      (if-let [evt-builder (:>evt opts)]
                        (>evt (evt-builder new-key))
                        ((:on-change opts) new-key))))

        value (or (if (contains? opts :value)
                    (:value opts (:default opts)))

                  (when-let [sub-form (:<sub opts)]
                    (or (<sub sub-form) (:default opts)))

                  (throw (ex-info "No current value provided to select" opts)))]

    (into [:select (-> opts
                       (assoc :on-change on-change
                              :value (value-from-children children value))
                       (dissoc :selected :<sub :>evt))]

          children)))
