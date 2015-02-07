(ns jubot.brain.memory
  (:require [jubot.brain.protocol :refer :all]))

(def ^:private mem (atom {}))

(defrecord MemoryBrain []
  Brain
  (set* [this k v] (swap! mem assoc k v))
  (get* [this k]   (get @mem k)))
