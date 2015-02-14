(ns jubot.brain.redis-test
  (:require
    [com.stuartsierra.component :as component]
    [jubot.brain.redis :refer :all]
    [taoensso.carmine  :as car]
    [conjure.core      :refer [stubbing]]
    [clojure.test      :refer :all]))

(def ^:private brain (map->RedisBrain {}))

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

  (testing "set/get"
    (let [brain (-> brain component/start)]
      (stubbing [car/set (fn [& _] (throw (Exception. "test exception")))
                 car/get (fn [& _] (throw (Exception. "test exception")))]
        (is (nil?  ((:set brain) "foo" "bar")))
        (is (nil?  ((:get brain) "foo")))))))
