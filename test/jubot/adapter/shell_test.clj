(ns jubot.adapter.shell-test
  (:require
    [jubot.adapter.shell :refer :all]
    [conjure.core        :refer [stubbing]]
    [clojure.test        :refer :all]))

(def ^:private botname "test")
(def ^:pricate this (->ShellAdapter botname))
(defn handler [_ text] (str "[" text "]"))

(deftest test-process-output
  (stubbing [println* str]
    (is (= (process-output this "foo") "test=>foo"))))

(deftest test-process-input
  (testing "ignore nil input"
    (is (nil? (process-input this handler nil))))

  (testing "ignore message which is not addressed to bot"
    (is (nil? (process-input this handler "foo"))))

  (testing "handler function returns nil"
    (is (nil? (process-input this (constantly nil) (str botname " foo")))))

  (testing "handler function returns string"
    (stubbing [process-output #(second %&)]
      (is (= (process-input this handler (str botname " foo"))
             "[foo]")))))
