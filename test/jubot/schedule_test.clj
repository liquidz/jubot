(ns jubot.schedule-test
  (:require
    [com.stuartsierra.component :as component]
    [jubot.scheduler :refer :all]
    [cronj.core      :as c]
    [conjure.core    :refer [stubbing]]
    [clojure.test    :refer :all]))

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

(def ^:private test-entries
  (schedules "foo" (constantly "bar")))

(deftest test-create-scheduler
  (testing "no entries"
    (let [s (create-scheduler {})]
      (are [x y] (= x y)
           jubot.scheduler.Scheduler (type s)
           []                        (:entries s))))

  (testing "with test-entries"
    (stubbing [c/start! nil, c/stop! nil]
      (let [started (component/start (create-scheduler {:entries test-entries}))
            stopped (component/stop started)]
        (are [x y] (= x y)
             false                (nil? (:cj started))
             (count test-entries) (count (:entries started))
             (first test-entries) (first (:entries started))
             true                 (nil? (:cj stopped)))))))

(deftest test-collect
  (do (create-ns 'jubot.test.a)
      (create-ns 'jubot.test.b)
      (intern 'jubot.test.a 'a-schedule
              (schedule "x" (constantly "xx")))
      (intern 'jubot.test.b 'b-schedule
              (schedules "y" (constantly "yy"), "z" (constantly "zz"))))

  (testing "public-schedules"
    (is (= (map resolve '(jubot.test.a/a-schedule jubot.test.b/b-schedule))
           (public-schedules #"^jubot\.test")))
    (is (= (map resolve '(jubot.test.a/a-schedule))
           (public-schedules #"^jubot\.test\.a"))))

  (testing "collect"
    (let [[a b c :as ls] (collect #"^jubot\.test")]
      (are [x y] (= x y)
           3    (count ls)
           "x"  (-> a meta :schedule)
           "xx" (a)
           "y"  (-> b meta :schedule)
           "yy" (b)
           "z"  (-> c meta :schedule)
           "zz" (c)))))
