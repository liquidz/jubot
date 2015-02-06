(ns jubot.brain.memory
  ;(:require [jubot.brain :refer :all])
  )

(def ^:private mem (atom {}))

(defrecord MemoryBrain []
  jubot.brain.Brain
  ;Brain
  (set* [this k v] (swap! mem assoc k v))
  (get* [this k]   (get @mem k)))
