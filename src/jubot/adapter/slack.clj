(ns jubot.adapter.slack
  (:require
    [jubot.adapter.util :refer [text-to-bot]]
    [jubot.redef :refer :all]
    [com.stuartsierra.component :as component]
    [ring.adapter.jetty       :refer [run-jetty]]
    [ring.middleware.defaults :refer :all]
    [compojure.core           :refer [defroutes GET POST]]
    [compojure.route          :refer [not-found]]
    [clojure.data.json        :as    json]
    [clj-http.lite.client     :as    client]))

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
                 :username (:name this)}]
    (when url
      (client/post url {:form-params {:payload (json/write-str payload)}}))))

(defn process-input
  [this handler-fn params]
  (let [{:keys [token user_name channel_name text]} params
        botname (:name this)
        option  {:user user_name :channel channel_name}]
    (or (some->> text
                 (not-from-slackbot user_name)
                 (valid-outgoing-token token)
                 (text-to-bot botname)
                 (assoc option :text)
                 handler-fn
                 (hash-map :username botname :text)
                 json/write-str)
        "")))

(defroutes app
  (GET "/" {:keys [adapter]}
    (str "this is jubot slack adapter."
         " bot's name is \"" (:name adapter) "\"."))
  (POST "/" {:keys [adapter handler-fn params]}
    (process-input adapter handler-fn params))
  (not-found "page not found"))

(defn wrap-adapter
  [handler adapter bot-handler]
  #(handler
     (assoc % :adapter    adapter
              :handler-fn bot-handler)))


(defrecord SlackAdapter [name server handler]
  component/Lifecycle
  (start [this]
    (if server
      this
      (do (println ";; start slack adapter. bot name is" name)
          (let [server (run-jetty
                         (-> app
                             (wrap-defaults api-defaults)
                             (wrap-adapter this handler))
                         {:port (Integer. (or (getenv* "PORT") DEFAULT_PORT))
                          :join? false })]
            (assoc this
                   :server server
                   :out    (partial process-output this))))))
  (stop [this]
    (if-not server
      this
      (do (println ";; stop slack adapter")
          (try (.stop server)
               (catch Exception e
                 (println ";; error occured when stopping server:" e)))
          (assoc this :server nil)))))
