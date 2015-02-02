(ns jubot.dev
  (:require
    [jubot.core :refer :all]
    [jubot.util.handler :refer :all]
    [jubot.brain :as brain]
    )
  )

(def handler
  (regexp-handler
    #"^ping$"            (constantly "pong")
    #"^set (.+?) (.+?)$" (fn [this [[_ k v]]] (brain/set k v))
    #"^get (.+?)$"       (fn [this [[_ k]]]   (brain/get k))
    :else                (constantly "unknown command")))

(def -main (jubot :handler handler))
