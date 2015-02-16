(ns jubot.scheduler.keep-awake-test
  (:require
    [jubot.scheduler.keep-awake :refer :all]
    [jubot.redef                :refer :all]
    [clojure.test               :refer :all]
    [conjure.core               :refer [stubbing]]
    [clj-http.lite.client       :as client]))

(deftest test-keep-awake-schedule
  (stubbing [client/get identity]
    (is (nil? (keep-awake-schedule))))

  (stubbing [getenv* "url"
             client/get identity]
    (is (= "url" (keep-awake-schedule)))))
