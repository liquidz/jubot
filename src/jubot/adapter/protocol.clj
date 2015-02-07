(ns jubot.adapter.protocol)

(defprotocol Adapter
  (start* [this handler-fn])
  (send*  [this text]))

(defmacro defadapter
  [name & body]
  `(defrecord ~name [~'botname]
     ~'Adapter
     ~@body))
