(ns jubot.adapter.slack
  (:require
    [jubot.adapter :refer :all]
    [ring.adapter.jetty     :refer [run-jetty]]
    [compojure.core         :refer [defroutes GET POST]]
    [compojure.route        :refer [not-found]]
    [compojure.handler      :refer [api]]
    [clojure.data.json      :as    json]
    [clj-http.lite.client   :as    client]))

(def ^:private DEFAULT_PORT 8080)
(def ^:private OUTGOING_TOKEN_KEY "SLACK_OUTGOING_TOKEN")
(def ^:private INCOMING_URL_KEY   "SLACK_INCOMING_URL")

; ------------------------------------------
; {:channel_id   "xxxxxxxxx",
;  :token        "xxxxxxxxxxxxxxxxxxxxxxxx",
;  :channel_name "test",
;  :user_id      "xxxxxxxxx",
;  :team_id      "xxxxxxxxx",
;  :service_id   "xxxxxxxxxx",
;  :user_name    "slackbot",
;  :team_domain  "uochan",
;  :timestamp    "1422058599.000004",
;  :text         "foo bar"}
; ------------------------------------------

(def getenv* #(System/getenv %))

(defn not-from-slackbot
  [username text]
  (if (not= "slackbot" username)
    text))

(defn valid-outgoing-token
  [token text]
  (if (= token (getenv* OUTGOING_TOKEN_KEY))
    text))

(defn process-output
  [this text]
  (let [url     (getenv* INCOMING_URL_KEY)
        payload {:text text
                 :username (:botname this)}]
    (when url
      (client/post url {:form-params {:payload (json/write-str payload)}}))))

(defn process-input
  [this handler-fn params]
  (let [{:keys [token user_name text]} params
        botname (:botname this)]
    (or (some->> text
                 (not-from-slackbot user_name)
                 (valid-outgoing-token token)
                 (text-to-bot botname)
                 handler-fn
                 (hash-map :username botname :text)
                 json/write-str)
        "")))

(defroutes app
  (GET "/" {:keys [adapter]}
    (str "this is jubot slack adapter."
         " bot's name is \"" (:botname adapter) "\"."))
  (POST "/" {:keys [adapter handler-fn params]}
    (process-input adapter handler-fn params))
  (not-found "page not found"))

(defn- with-adapter
  [handler adapter bot-handler]
  #(handler
     (assoc % :adapter    adapter
              :handler-fn bot-handler)))

(defadapter SlackAdapter
  (start* [this handler-fn]
          (run-jetty
            (-> app api (with-adapter this handler-fn))
            {:port  (Integer. (or (getenv* "PORT") DEFAULT_PORT))
             :join? false}))
  (send* [this text]
         (process-output this text)))
