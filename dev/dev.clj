(ns dev
  (:require
    [jubot.core      :refer [jubot]]
    [jubot.handler   :as jh]
    [jubot.adapter   :as ja]
    [jubot.brain     :as jb]
    [jubot.scheduler :as js]))

(defn ping-handler
  "jubot ping - reply with 'pong'"
  [{:keys [text message-for-me?]}]
  (if (and message-for-me? (= "ping" text)) "pong"))

(defn brain-handler
  "jubot set <key> <value> - store data to brain
   jubot get <key> - restore data from brain"
  [{:keys [message-for-me?] :as arg}]
  (when message-for-me?
    (jh/regexp arg
      #"^set (.+?) (.+?)$" (fn [{[_ k v] :match}] (jb/set k v))
      #"^get (.+?)$"       (fn [{[_ k]   :match}] (jb/get k)))))

(defn hear-handler
  [{:keys [text user]}]
  (when (re-find #"hello" text)
    (ja/out (str "hello " user) :as "world")))

;(def dev-schedule
;  (js/schedules
;    "/5 * * * * * *" #(str "Hey!")))

(def -main (jubot :ns-regexp #"^dev"))
