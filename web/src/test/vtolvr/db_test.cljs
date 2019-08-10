(ns vtolvr.db-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [vtolvr.db :refer [default-db]]))

(deftest db-test
  (testing "Rough test"
    (is (not (nil? default-db)))))
