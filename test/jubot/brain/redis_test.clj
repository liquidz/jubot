(ns jubot.brain.redis-test
  (:require
    [com.stuartsierra.component :as component]
    [jubot.brain.redis :refer :all]
    [taoensso.timbre      :as timbre]
    [taoensso.carmine  :as car]
    [conjure.core      :refer [stubbing]]
    [clojure.test      :refer :all]))

(def ^:private brain (map->RedisBrain {}))
(timbre/set-config!
  [:appenders :standard-out :enabled?] false)

(deftest test-RedisBrain
  (testing "start brain"
    (let [brain (component/start brain)]
      (are [k] (contains? brain k)
           :conn
           :set
           :get)
      (is (= brain (component/start brain)))))

  (testing "stop brain"
    (let [brain (-> brain component/start component/stop)]
      (are [k] (nil? (get brain k))
           :conn
           :set
           :get)
      (is (= brain (component/stop brain)))))

  (testing "specify redis uri"
    (is (= DEFAULT_REDIS_URI (-> brain component/start :conn :spec :uri)))
    (is (= "foo" (-> (map->RedisBrain {:uri "foo"})
                     component/start :conn :spec :uri))))

  (testing "set/get/keys"
    (let [brain (-> brain component/start)]
      (stubbing [car/set (fn [& _] (throw (Exception. "test")))
                 error*  "OK"]
        (is (= "OK" ((:set brain) "foo" "bar"))))

      (stubbing [car/get (fn [& _] (throw (Exception. "test")))
                 error*  "bar"]
        (is (= "bar" ((:get brain) "foo"))))

      (stubbing [car/keys (fn [& _] (throw (Exception. "test")))
                 error*   ["foo"]]
        (is (= ["foo"] ((:keys brain))))))))
