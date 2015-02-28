(ns jubot.schedule-test
  (:require
    [com.stuartsierra.component :as component]
    [jubot.scheduler :refer :all]
    [jubot.adapter   :as ja]
    [cronj.core      :as c]
    [conjure.core    :refer [stubbing]]
    [clojure.test    :refer :all]))

(defn test-ns-fixture
  [f]
  (create-ns 'jubot.test.scheduler.a)
  (create-ns 'jubot.test.scheduler.b)
  (create-ns 'jubot.test.scheduler.b-test)
  (intern 'jubot.test.scheduler.a 'a-schedule
          (schedule "x" (constantly "xx")))
  (intern 'jubot.test.scheduler.b 'b-schedule
          (schedules "y" (constantly "yy"), "z" (constantly "zz")))
  (intern 'jubot.test.scheduler.b-test 'must-not-be-collected-schedule
          (schedule "z" (constantly nil)))
  (f)
  (remove-ns 'jubot.test.scheduler.a)
  (remove-ns 'jubot.test.scheduler.b)
  (remove-ns 'jubot.test.scheduler.b-test))

(use-fixtures :each test-ns-fixture)

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

(deftest test-schedule->task
  (testing "return string"
    (stubbing [ja/out #(str "[" % "]")]
      (let [s (schedule "foo" (constantly "bar"))
            t (schedule->task s)]
        (are [x y] (= x y)
             "foo"   (:schedule t)
             "[bar]" ((:handler t) nil nil)))))

  (testing "not return string"
    (stubbing [ja/out (fn [_] (throw (Exception. "must not be called")))]
      (let [s (schedule "foo" (constantly [1 2 3]))
            t (schedule->task s)]
        (are [x y] (= x y)
             "foo" (:schedule t)
             nil   ((:handler t) nil nil))))))

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

(deftest test-public-schedules
  (is (= (map resolve '(jubot.test.scheduler.a/a-schedule
                         jubot.test.scheduler.b/b-schedule))
         (sort #(.compareTo (-> %1 meta :name) (-> %2 meta :name))
               (public-schedules #"^jubot\.test\.scheduler"))))
  (is (= (map resolve '(jubot.test.scheduler.a/a-schedule))
         (public-schedules #"^jubot\.test\.scheduler\.a"))))

(deftest test-collect
  (let [ls (sort #(.compareTo (-> %1 meta :schedule) (-> %2 meta :schedule))
                 (collect #"^jubot\.test\.scheduler"))
        [a b c] ls]
    (are [x y] (= x y)
         3    (count ls)
         "x"  (-> a meta :schedule)
         "xx" (a)
         "y"  (-> b meta :schedule)
         "yy" (b)
         "z"  (-> c meta :schedule)
         "zz" (c)))
  (is (empty? (collect #"^foobar"))))
