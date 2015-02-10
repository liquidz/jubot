(ns jubot.brain-test
  (:require
    [jubot.brain       :as brain]
    [jubot.system      :refer :all]
    [jubot.system-test :refer [with-mocked-system]]
    [clojure.test      :refer :all]))

(deftest test-create-brain
  (testing "redis brain"
    (is (= jubot.brain.redis.RedisBrain
           (type (brain/create-brain {:brain "redis"})))))
  (testing "default(memory) brain"
    (is (= jubot.brain.memory.MemoryBrain
           (type (brain/create-brain {}))))))

(deftest test-set
  (testing "not started brain"
    (is (nil? (brain/set "foo" "bar"))))

  (testing "dummy brain"
    (with-mocked-system {:brain {:set list}}
      (fn []
        (is (= ["foo" "bar"] (brain/set "foo" "bar")))))))

(deftest test-get
  (testing "not started brain"
    (is (nil? (brain/get "foo"))))

  (testing "dummy brain"
    (with-mocked-system {:brain {:get list}}
      (fn []
        (is (= ["foo"] (brain/get "foo")))))))
