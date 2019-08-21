(ns vtolvr-compiler.edn-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [vtolvr-compiler.edn :refer :all]))

(defn process-string [s]
  (process-stream
    (io/input-stream (.getBytes s))))

(defn ->index [section-files]
  (->> section-files
       (filter (comp #{"index.edn"} first))

       ; first (only) match
       first

       ; get the value from the pair
       second))

(defn ->index-sections [section-files]
  (->> section-files

       ->index

       ; the index is in :sections
       :sections

       (map (fn [[_ {:keys [title]}]]
              title))))

(deftest combine-path-test
  (testing "Initial path"
    (is (= ["Serenity"]

           (combine-path
             nil
             0
             "Serenity"))))

  (testing "0-depth"
    (is (= ["Serenity"]

           (combine-path
             {:level 1
              :path ["Boat" "Firefly"]}
             0
             "Serenity"))))

  (testing "Increase depth"
    (is (= ["Boat" "Firefly" "Serenity"]

           (combine-path
             {:level 1
              :path ["Boat" "Firefly"]}
             2
             "Serenity"))))

  (testing "Match depth"
    (is (= ["Boat" "Firefly" "Shuttle"]

           (combine-path
             {:level 2
              :path ["Boat" "Firefly" "Serenity"]}
             2
             "Shuttle"))))

  (testing "Decrease depth by one"
    (is (= ["Boat" "Shuttle"]

           (combine-path
             {:level 2
              :path ["Boat" "Firefly" "Serenity"]}
             1
             "Shuttle"))))

  (testing "Decrease depth by multiple"
    (is (= ["Boat" "Komodo"]

           (combine-path
             {:level 3
              :path ["Boat" "Firefly" "Serenity" "Stash"]}
             1
             "Komodo")))))

(deftest path->file-test
  (testing "Single path"
    (is (= "serenity.edn"
           (path->file ["Serenity"])))

    (is (= "firefly-class-ship-serenity.edn"
           (path->file ["'Firefly'-class   ship, \"Serenity\""]))))

  (testing "Multi path"
    (is (= "ships/firefly-class/serenity.edn"
           (path->file ["Ships" "'Firefly'-class" "Serenity"])))))

(deftest process-stream-test
  (testing "Extract sections"
    (let [processed (process-string "
# Introduction

An Intro

A paragraph

# Cargo Manifest

- Bobble-headed Geisha dolls

# Crew Manifest

- Mal Reynolds
                  ")]

      (is (not (nil? (:intro (->index processed)))))
      (is (= ["Cargo Manifest"
              "Crew Manifest"]

             (->index-sections processed)))))

  (testing "Extract nested (empty) sections"
    (let [processed (process-string "
# Ship

Many ships

## Firefly

### Serenity

#### Cargo

- Bobble-headed geisha dolls

#### Crew

- Captain: Mal Reynolds
                  ")]

      (is (nil? (:intro (->index processed))))
      (is (= ["Ship"]
             (->index-sections processed))))))
