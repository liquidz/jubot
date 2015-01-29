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

(deftest test-not-from-slackbot
  (are [x y] (= x y)
       "bar" (not-from-slackbot "foo" "bar")
       nil   (not-from-slackbot "foo" nil)
       nil   (not-from-slackbot "slackbot" "bar")))

(deftest test-valid-outgoing-token
  (are [x y] (= x y)
       "foo" (valid-outgoing-token nil "foo")
       nil   (valid-outgoing-token "foo" "bar"))

  (stubbing [getenv* "foo"]
    (are [x y] (= x y)
         nil   (valid-outgoing-token nil "foo")
         "bar" (valid-outgoing-token "foo" "bar"))))

(deftest test-process-input
  (testing "ignore nil input"
    (is (= "" (process-input* handler {:text nil}))))

  (testing "ignore message from slackbot"
    (is (= "" (process-input*
                handler {:text (str botname " foo") :user_name "slackbot"}))))

  (testing "ignore message with invalid token"
    (is (= "" (process-input*
                handler {:text (str botname " foo") :token "invalid"}))))

  (testing "ignore message which is not addressed to bot"
    (is (= "" (process-input* handler {:text "foo"}))))

  (testing "handler function returns nil"
    (is (= "" (process-input* nil-handler {:text (str botname " foo")}))))

  (testing "handler function returns string"
    (is (= (json/write-str {:text "[foo]"})
           (process-input* handler {:text (str botname " foo")})))))

;;; TODO: output-process
