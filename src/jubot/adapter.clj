(ns jubot.adapter)

(def ^:dynamic *adapter* nil)

(defprotocol Adapter
  (start! [this handler-fn])
  (send!  [this text]))

(defn start-adapter
  [adapter handler-fn]
  (binding [*adapter* adapter]
    (start! *adapter* handler-fn)))
