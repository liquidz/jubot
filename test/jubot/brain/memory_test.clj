(ns jubot.brain.memory-test
  (:require
    [com.stuartsierra.component :as component]
    [jubot.brain.memory :refer :all]
    [clojure.test       :refer :all]))

(def ^:private brain (map->MemoryBrain {}))

(deftest test-MemoryBrain
  (testing "start brain"
    (let [brain (component/start brain)]
      (are [k] (contains? brain k)
           :mem
           :set
           :get)
      (is (= brain (component/start brain)))))

  (testing "stop brain"
    (let [brain (-> brain component/start component/stop)]
      (are [k] (nil? (get brain k))
           :mem
           :set
           :get)
      (is (= brain (component/stop brain)))))

  (testing "set/get/keys"
    (let [brain (component/start brain)]
      (is (= {"foo" "bar"} ((:set brain) "foo" "bar")))
      (is (= "bar"         ((:get brain) "foo")))
      (is (= ["foo"]       ((:keys brain)))))))
