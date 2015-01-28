(ns jubot.adapter.slack-test
  (:require
    [jubot.adapter.slack :refer :all]
    [conjure.core        :refer [stubbing]]
    [midje.sweet         :refer :all]
    [clojure.data.json   :as json]))

(def ^:private botname "test")
(def ^:pricate this (->SlackAdapter botname))
(defn- handler [_ text] (str "[" text "]"))
(defn- text [& s] {:text (apply str s)})

(facts "process-input should work fine."
  (fact "ignore nil input"
    (process-input this handler {:text nil}) => "")

  (fact "ignore message which is not addressed to bot"
    (process-input this handler {:text "foo"}) => "")

  (fact "handler function returns nil"
    (process-input
      this (constantly nil)
      {:text (str botname " foo")}) => "")

  (fact "handler function returns string"
    (process-input
      this handler
      {:text (str botname " foo")}) => (json/write-str {:text "[foo]"})))

;; TODO: output-process
