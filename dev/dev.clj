(ns dev
  (:require
    [jubot.core      :refer [jubot]]
    [jubot.handler   :as jh]
    [jubot.adapter   :as ja]
    [jubot.brain     :as jb]
    [jubot.scheduler :as js]))

(defn ping-handler
  "jubot ping - reply with 'pong'"
  [{text :text}]
  (if (= "ping" text) "pong"))

(def ^{:doc (str "jubot set <key> <value> - fixme\n"
                 "jubot get <key> - fixme")}
  brain-handler
  (jh/regexp
    #"^set (.+?) (.+?)$" (fn [{[_ k v] :match}] (jb/set k v))
    #"^get (.+?)$"       (fn [{[_ k]   :match}] (jb/get k))))

;(def dev-schedule
;  (js/schedules
;    "/5 * * * * * *" #(ja/out "KITERU")))

(def -main (jubot :ns-regexp #"^dev"))
