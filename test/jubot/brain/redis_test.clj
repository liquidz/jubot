(ns jubot.brain.redis-test
  (:require
    [com.stuartsierra.component :as component]
    [jubot.brain.redis :refer :all]
    ;[taoensso.carmine  :as car]
    ;[conjure.core      :refer [stubbing]]
    [clojure.test      :refer :all]))

(def ^:private brain (map->RedisBrain {}))

(deftest test-RedisBrain
  (testing "start brain"
    (let [brain (component/start brain)]
      (are [k] (contains? brain k)
           :conn
           :set
           :get)))

  (testing "stop brain"
    (let [brain (-> brain component/start component/stop)]
      (are [k] (nil? (get brain k))
           :conn
           :set
           :get)))

  (testing "specify redis uri"
    (is (= DEFAULT_REDIS_URI (-> brain component/start :conn :uri)))
    (is (= "foo" (-> (map->RedisBrain {:uri "foo"}) component/start :conn :uri))))

  (testing "set/get"
    ;; TODO
    ))
