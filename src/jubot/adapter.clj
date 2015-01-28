(ns jubot.adapter)

(def ^:dynamic *adapter* nil)

(defprotocol Adapter
  (start! [this handler-fn])
  (send!  [this text]))

(defmacro defadapter
  [name & body]
  `(defrecord ~name [~'botname]
     ~'Adapter
     ~@body))

(defn text-to-bot
  [botname text]
  (let [prefix (str botname " ")]
    (if (and (string? text) (.startsWith text prefix))
      (apply str (drop (count prefix) text)))))

(defn start-adapter
  [adapter handler-fn]
  (binding [*adapter* adapter]
    (start! *adapter* handler-fn)))
