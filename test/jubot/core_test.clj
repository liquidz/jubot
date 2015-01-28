(ns jubot.core-test
  (:require
    [jubot.core    :refer :all]
    [jubot.adapter :refer [start-adapter]]
    [conjure.core  :refer [stubbing]]
    [midje.sweet   :refer :all]))

(facts "jubot should work fine."
  (stubbing [start-adapter list]
    (fact "default adapter"
      (let [[adapter handler] (jubot "handler")]
        (type adapter) => jubot.adapter.shell.ShellAdapter
        handler        => "handler"))

    (fact "slack adapter"
      (let [[adapter handler] (jubot "handler" "-a" "slack")]
        (type adapter) => jubot.adapter.slack.SlackAdapter
        handler        => "handler"))))

