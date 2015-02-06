(ns jubot.dev
  (:require
    [jubot.core :refer :all]
    [jubot.adapter :as adapter]
    [jubot.util.handler :refer :all]
    [jubot.brain :as brain]
    [jubot.schedule :as js]
    )
  )

(def handler
  (regexp-handler
    #"^ping$"            (constantly "pong")
    #"^set (.+?) (.+?)$" (fn [[[_ k v]]] (brain/set k v))
    #"^get (.+?)$"       (fn [[[_ k]]]   (brain/get k))
    :else                (constantly "unknown command")))

(def kiteru
  (with-meta
    (fn [] (adapter/send! "kiteru"))
    {:schedule "/5 * * * * * *"}))

;(schedules
;  "..." (fn [] ...)
;  ",,," (fn [] ,,,))

(def -main (jubot :handler handler
                  :schedule [kiteru]
                  ))
