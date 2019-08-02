(ns vtolvr-compiler.edn-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [vtolvr-compiler.edn :refer :all]))

(defn process-string [s]
  (process-stream
    (io/input-stream (.getBytes s))))

(defn ->index-contents [section-files]
  (->> section-files

       (filter (comp #{"index.edn"} first))

       ; first (only) match
       first

       ; get the value from the pair
       second))

(deftest process-stream-test
  (testing "Extract sections"
    (is (= ["Introduction"
            "Cargo Manifest"
            "Crew Manifest"]

           (->> (process-string
                 "
# Introduction

An Intro

A paragraph

# Cargo Manifest

- Bobble-headed Geisha dolls

# Crew Manifest

- Mal Reynolds
                  ")

                ->index-contents
                keys)))))
