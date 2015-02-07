(ns jubot.brain.protocol)

(defprotocol Brain
  (set* [this k v])
  (get* [this k]))
