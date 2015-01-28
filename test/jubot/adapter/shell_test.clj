(ns jubot.adapter.shell-test
  (:require
    [jubot.adapter.shell :refer :all]
    [conjure.core        :refer [stubbing]]
    [midje.sweet         :refer :all]))

(def ^:private botname "test")
(def ^:pricate this (->ShellAdapter botname))
(defn handler [_ text] (str "[" text "]"))

(fact "process-output should work fine."
  (stubbing [println* str]
    (process-output this "foo") => "test=>foo"))

(facts "process-input should work fine."
  (fact "ignore nil input"
    (process-input this handler nil) => nil)

  (fact "ignore message which is not addressed to bot"
    (process-input this handler "foo") => nil)

  (fact "handler function returns nil"
    (process-input this (constantly nil) (str botname " foo")) => nil)

  (fact "handler function returns string"
    (stubbing [process-output #(second %&)]
      (process-input this handler (str botname " foo")) => "[foo]")))

