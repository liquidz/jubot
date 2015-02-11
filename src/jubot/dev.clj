(ns jubot.dev
  (:require
    [jubot.core :as core]
    [jubot.handler :refer [regexp-handler]]
    [jubot.adapter :as adapter]
    [jubot.brain :as brain]
    [jubot.scheduler :as js]
    ))

(def handler
  (regexp-handler
    #"^ping$"            (constantly "PONG!!!!!!PONG!!!!!")
    #"^set (.+?) (.+?)$" (fn [{[[_ k v]] :match}] (brain/set k v))
    #"^get (.+?)$"       (fn [{[[_ k]] :match}]   (brain/get k))
    :else                (constantly "unknown command")))

(def schedule
  (js/schedules
    "/5 * * * * * *" #(adapter/out "KITERU")))

(def -main (core/jubot
             :handler handler
             :entries [];schedule
             ))
