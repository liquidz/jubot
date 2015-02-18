(ns jubot.test-test
  (:require
    [jubot.test   :refer :all]
    [jubot.brain  :as jb]
    [clojure.test :refer :all]))

(deftest test-with-test-brain
  (with-test-brain
    (are [x y] (= x y)
         {"a" "b"}          (jb/set "a" "b")
         {"a" "b", "b" "c"} (jb/set "b" "c")
         "b"                (jb/get "a")
         "c"                (jb/get "b")))
  (with-test-brain
    (is (nil? (jb/get "a")))))
