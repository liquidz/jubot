(ns dev
  (:require
    [jubot.core      :refer [jubot]]
    [jubot.handler   :as jh]
    [jubot.adapter   :as ja]
    [jubot.brain     :as jb]
    [jubot.scheduler :as js]))

(def dev-handler
  (jh/regexp
    #"^ping$"            (constantly "pong")
    #"^this$"            (fn [opt] (str opt))
    #"^set (.+?) (.+?)$" (fn [{[_ k v] :match}] (jb/set k v))
    #"^get (.+?)$"       (fn [{[_ k]   :match}] (jb/get k))
    :else                (constantly "unknown command")))

;(def dev-schedule
;  (js/schedules
;    "/5 * * * * * *" #(ja/out "KITERU")))

(def -main (jubot :ns-regexp #"^dev"))
