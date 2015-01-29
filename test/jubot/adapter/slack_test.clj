(ns jubot.adapter.slack-test
  (:require
    [jubot.adapter.slack :refer :all]
    [conjure.core        :refer [stubbing]]
    [clojure.data.json   :as json]
    [clojure.test        :refer :all]))

(def ^:private botname "test")
(def ^:private handler #(str "[" %2 "]"))
(def ^:private nil-handler (constantly nil))
(def ^:private process-input* (partial process-input (->SlackAdapter botname)))

(deftest text-process-input
  (testing "ignore nil input"
    (is (= "" (process-input* handler {:text nil}))))

  (testing "ignore message from slackbot"
    (is (= "" (process-input*
                handler {:text (str botname " foo") :user_name "slackbot"}))))

  (testing "ignore message which is not addressed to bot"
    (is (= "" (process-input* handler {:text "foo"}))))

  (testing "handler function returns nil"
    (is (= "" (process-input* nil-handler {:text (str botname " foo")}))))

  (testing "handler function returns string"
    (is (= (json/write-str {:text "[foo]"})
           (process-input* handler {:text (str botname " foo")})))))

;;; TODO: output-process
