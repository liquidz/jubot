(ns jubot.adapter.slack-test
  (:require
    [jubot.adapter.slack  :refer :all]
    [conjure.core         :refer [stubbing]]
    [clojure.data.json    :as    json]
    [clojure.test         :refer :all]
    [clj-http.lite.client :refer [post]]))

(def ^:private botname "test")
(def ^:private handler #(str "[" % "]"))
(def ^:private nil-handler (constantly nil))
(def ^:private adapter (->SlackAdapter botname))
(def ^:private process-input* (partial process-input adapter))
(def ^:private process-output* (partial process-output adapter))

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
    (is (= {:username botname :text "[foo]"}
           (json/read-str
             (process-input* handler {:text (str botname " foo")})
             :key-fn keyword)))))


(deftest test-process-output
  (testing "should work fine"
    (stubbing [getenv* "localhost"
               post    list]
      (let [[url {{payload :payload} :form-params}] (process-output* "foo")
            payload (json/read-str payload :key-fn keyword)]
        (is (= "localhost" url))
        (is (= {:username botname :text "foo"} payload)))))

  (testing "INCOMING_URL_KEY is not defined"
    (stubbing [getenv* nil]
      (is (nil? (process-output* "foo"))))))
