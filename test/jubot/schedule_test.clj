(ns jubot.schedule-test
  (:require
    [jubot.schedule :refer :all]
    [cronj.core     :refer [cronj start!]]
    [conjure.core   :refer [stubbing]]
    [clojure.test   :refer :all]))

(deftest test-schedule
  (let [f (schedule "foo" (constantly "bar"))]
    (is (= "bar" (f)))
    (is (= "foo" (-> f meta :schedule)))))

(deftest test-schedules
  (let [fs (schedules "foo" (constantly "bar")
                      "bar" (constantly "baz"))]
    (are [x y] (= x y)
         "bar" ((first fs))
         "foo" (-> fs first meta :schedule)
         "baz" ((second fs))
         "bar" (-> fs second meta :schedule))))

(deftest test-start-schedule!
  (testing "with some schedules"
    (stubbing [cronj hash-map
               start! identity]
      (let [entries (schedules "foo" (constantly "bar")
                               "bar" (constantly "baz"))
            [entry _ :as entries] (:entries (start-schedule! "adapter" entries))]
        (are [x y] (= x y)
             2         (count entries)
             "bar"     ((:handler entry) nil {})
             "foo"     (:schedule entry)))))

  (testing "without schedule"
    (is (nil? (start-schedule! "adapter" [])))))
