(ns jubot.brain.memory
  )

(def ^:private mem (atom {}))

(defrecord MemoryBrain []
  jubot.brain.Brain
  (set* [this k v] (swap! mem assoc k v))
  (get* [this k]   (get @mem k)))
