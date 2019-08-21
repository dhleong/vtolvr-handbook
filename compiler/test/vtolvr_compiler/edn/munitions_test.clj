(ns vtolvr-compiler.edn.munitions-test
  (:require [clojure.test :refer :all]
            [vtolvr-compiler.edn.munitions :refer :all]))

(def ^:private fake-data
  {"Air-to-Air"
   {:contents [:div "Info"]

    "Employment"
    {"Heat-seeking"
     {:contents [:div "Info"]}}

    "AIM-9"
    {:contents [:div
                [:p "The Sidewinder is a standard, heat-seeking air-to-air missile."]
                [:table
                 [:thead
                  [:tr
                   [:th {} "Attribute"]
                   [:th {} "Value"]]]
                 [:tbody
                  [:tr [:td {} "Type" ] [:td {} "air-to-air"]]
                  [:tr [:td {} "Guidance" ] [:td {} "infrared heat-seeking"]]
                  [:tr [:td {} "Fire-and-forget" ] [:td {} "Yes"]]
                  [:tr [:td {} "Cost" ] [:td {} "$850 / missile"]]
                  [:tr [:td {} "Mass" ] [:td {} "120kg"]]
                  [:tr [:td {} "Radio Call" ] [:td {} "Fox Two"]]]]]}}

   "Air-to-Surface"
   {"GPS-guided"
    {"Employment: GPS-guided Bombs"
     {:contents [:div "Info"]}

     "Employment: GPS-guided Cruise Missiles"
     {:contents [:div "Info"]

      "Cruise Missile Attack Modes"
      {:contents [:div "Info"]}}}}})

(deftest post-process-test
  (testing "Index all munitions"
    (is (= ["AIM-9"]

           (->> fake-data
                post-process
                :munitions
                (map :name)))))

  (testing "Index all notes"
    (is (= [["Air-to-Air"]
            ["Air-to-Air" "Employment" "Heat-seeking"]
            ["Air-to-Surface" "GPS-guided" "Employment: GPS-guided Bombs"]
            ["Air-to-Surface" "GPS-guided"
             "Employment: GPS-guided Cruise Missiles"]
            ["Air-to-Surface" "GPS-guided"
             "Employment: GPS-guided Cruise Missiles"
             "Cruise Missile Attack Modes"]]

           (->> fake-data
                post-process
                :notes
                (map :path)))))

  (testing "Extract munitions attributes"
    (is (= {:type :air-to-air
            :guidance [:infrared :heat-seeking]
            :fire-and-forget true
            :cost "$850 / missile"
            :mass "120kg"
            :radio-call "Fox Two"}

           (->> fake-data
                post-process
                :munitions
                first
                :attrs)))))

(deftest process-attr-val-test
  (testing "Process guidance types"
    (is (= nil (process-attr-val :guidance "?")))
    (is (= [:infrared] (process-attr-val :guidance "infrared")))
    (is (= [:infrared :heat-seeking]
           (process-attr-val :guidance "infrared heat-seeking"))))

  (testing "Process Fire-and-forget"
    (is (= nil (process-attr-val :fire-and-forget "?")))
    (is (= true (process-attr-val :fire-and-forget "Yes")))
    (is (= false (process-attr-val :fire-and-forget [:em "No"])))))
