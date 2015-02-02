(ns jubot.dev
  (:require
    [jubot.core :refer :all]
    [jubot.adapter :refer :all]
    [jubot.util.handler :refer :all]
    [jubot.brain :as brain]
    [jubot.schedule :as js]
    )
  )

(def handler
  (regexp-handler
    #"^ping$"            (constantly "pong")
    #"^set (.+?) (.+?)$" (fn [this [[_ k v]]] (brain/set k v))
    #"^get (.+?)$"       (fn [this [[_ k]]]   (brain/get k))
    :else                (constantly "unknown command")))

(def kiteru
  (with-meta
    (fn [this] (send! this "kiteru"))
    {:schedule "* * * * * * *"}))

;(schedules
;  "..." (fn [] ...)
;  ",,," (fn [] ,,,))

(def -main (jubot :handler handler
                  :schedule [kiteru]
                  ))
