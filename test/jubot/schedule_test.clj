(ns jubot.schedule-test
  (:require
    [jubot.schedule :refer :all]
    [cronj.core     :refer [cronj start!]]
    [conjure.core   :refer [stubbing]]
    [clojure.test   :refer :all]))

(deftest test-clear-schedule!
  (is (= [] (clear-schedule!))))

(deftest test-set-schedule!
  (let [[a]   (do (clear-schedule!) (set-schedule! "foo" inc))
        [b c] (set-schedule! "bar" dec)
        ]
    (are [x y] (= x y)
         "foo" (-> a meta :schedule)
         "foo" (-> b meta :schedule)
         "bar" (-> c meta :schedule)
         2     (a 1)
         2     (b 1)
         0     (c 1)
         a     b)))

(deftest test-start-schedule!
  (stubbing [cronj hash-map
             start! identity]
    (clear-schedule!)
    (set-schedule! "foo" (constantly "bar"))
    (set-schedule! "bar" (constantly "baz"))

    (let [[entry _ :as entries] (:entries (start-schedule! "adapter"))]
      (are [x y] (= x y)
           2         (count entries)
           "bar"     ((:handler entry) nil {})
           "foo"     (:schedule entry)
           "adapter" (-> entry :opts :adapter)))))
