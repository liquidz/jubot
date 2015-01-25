(ns jubot.adapter.slack
  (:require
    [jubot.adapter :refer :all]
    [ring.adapter.jetty     :refer [run-jetty]]
    [compojure.core         :refer [defroutes GET POST]]
    [compojure.route        :refer [not-found]]
    [compojure.handler      :refer [api]]
    [clojure.data.json      :as json]))

(def ^:private DEFAULT_PORT 8080)


;{:channel_id "xxxxxxxxx",
; :token "xxxxxxxxxxxxxxxxxxxxxxxx",
; :channel_name "test",
; :user_id "xxxxxxxxx",
; :team_id "xxxxxxxxx",
; :service_id "xxxxxxxxxx",
; :user_name "slackbot",
; :team_domain "uochan",
; :timestamp "1422058599.000004",
; :text "foo bar"}


(defn process-request
  [{:keys [this handler params]}]
  ; TODO: token validation
  ; TODO: botname
  ;(let [{:keys [token user_name text]} params]
  ;  )

  (let [resp (handler this params)]
    (if (string? resp)
      (json/write-str
        {:text resp})
      "")))

(defroutes app
  (GET "/" [] "this is jubot slack adapter")
  (POST "/" req (process-request req))
  (not-found "page not found"))

(defn- with-adapter
  [handler adapter bot-handler]
  #(handler
     (assoc % :this    adapter
              :handler bot-handler)))

(defrecord SlackAdapter [botname]
  Adapter
  (start! [this handler-fn]
    (run-jetty
      (-> app api (with-adapter this handler-fn))
      {:port  (Integer. (or (System/getenv "PORT") DEFAULT_PORT))
       :join? false}))
  (send! [this text]
    ; FIXME
    nil
    )
  )
