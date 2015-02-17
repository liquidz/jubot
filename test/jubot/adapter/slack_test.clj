(ns jubot.adapter.slack-test
  (:require
    [com.stuartsierra.component :as component]
    [jubot.adapter.slack  :refer :all]
    [jubot.redef          :refer :all]
    [conjure.core         :refer [stubbing]]
    [clojure.data.json    :as    json]
    [clojure.test         :refer :all]
    [clj-http.lite.client :refer [post]]
    [ring.adapter.jetty   :refer [run-jetty]]
    [ring.middleware.defaults :refer :all]
    [ring.mock.request    :refer [request]]))

(def ^:private botname "test")
(def ^:private handler (fn [{:keys [user channel text]}]
                         (str "channel=" channel ",text=" text)))
(def ^:private nil-handler (constantly nil))
(def ^:private adapter (map->SlackAdapter {:name botname}))
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
    (is (= {:username botname :text "aaa: channel=bbb,text=ccc"}
           (json/read-str
             (process-input* handler {:text (str botname " ccc")
                                      :user_name "aaa"
                                      :channel_name "bbb"})
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

(deftest test-app
  (testing "get"
    (is (= 200 (-> (request :get "/") app :status))))

  (testing "post"
    (let [param {:foo "bar"}
          app   (-> app
                    (wrap-defaults api-defaults)
                    (wrap-adapter "adapter" "handler"))]
      (stubbing [process-input list]
        (is (= ["adapter" "handler" param]
               (-> (request :post "/" param) app :body)))))))

(deftest test-SlackAdapter
  (stubbing [run-jetty "jetty"]
    (testing "start adapter"
      (let [adapter (component/start adapter)]
        (are [x y] (= x y)
             "jetty" (:server adapter)
             true    (fn? (:out adapter)))
        (is (= adapter (component/start adapter)))))

    (testing "stop adapter"
      (let [adapter (-> adapter component/start component/stop)]
        (is (nil? (:server adapter)))
        (is (= adapter (component/stop adapter)))))))
