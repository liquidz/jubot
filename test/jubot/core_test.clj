(ns jubot.core-test
  (:require
    [jubot.core    :refer :all]
    [jubot.adapter :refer [start-adapter]]
    [conjure.core  :refer [stubbing]]
    [clojure.test  :refer :all]))

(deftest test-jubot
  (stubbing [start-adapter list]
    (testing "default adapter"
      (let [[adapter handler] (jubot "handler")]
        (are [x y] (= x y)
             (type adapter) jubot.adapter.shell.ShellAdapter
             handler        "handler")))

    (testing "slack adapter"
      (let [[adapter handler] (jubot "handler" "-a" "slack")]
        (are [x y] (= x y)
             (type adapter) jubot.adapter.slack.SlackAdapter
             handler        "handler")))))
