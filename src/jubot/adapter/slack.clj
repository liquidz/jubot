(ns jubot.adapter.slack
  (:require
    [jubot.adapter :refer :all]
    [ring.adapter.jetty     :refer [run-jetty]]
    [compojure.core         :refer [defroutes GET POST]]
    [compojure.route        :refer [not-found]]
    [compojure.handler      :refer [api]]
    [clojure.data.json      :as json]))

(def ^:private DEFAULT_PORT 8080)

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

(defn not-from-slackbot
  [username text]
  (if (not= "slackbot" username)
    text))

(defn process-input
  [this handler-fn params]
  ; TODO: token validation
  (let [{:keys [token user_name text]} params
        botname (:botname this)]
    (or (some->> text
                 (not-from-slackbot user_name)
                 (text-to-bot botname)
                 (handler-fn this)
                 (hash-map :text)
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
  (start! [this handler-fn]
          (run-jetty
            (-> app api (with-adapter this handler-fn))
            {:port  (Integer. (or (System/getenv "PORT") DEFAULT_PORT))
             :join? false}))
  (send! [this text]
         ; FIXME
         nil))
