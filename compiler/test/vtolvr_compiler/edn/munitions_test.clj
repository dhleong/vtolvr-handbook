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
    {:contents [:div "Sidewinder"]}}})

(deftest post-process-test
  (testing "Index all munitions"
    (is (= ["AIM-9"]

           (->> fake-data
                post-process
                :munitions
                (map :name)))))

  (testing "Index all notes"
    (is (= [["Air-to-Air"]
            ["Air-to-Air" "Employment" "Heat-seeking"]]

           (->> fake-data
                post-process
                :notes
                (map :path))))))

