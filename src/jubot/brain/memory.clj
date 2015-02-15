(ns jubot.brain.memory
  "Jubot brain for memory."
  (:require
    [com.stuartsierra.component :as component]))

(defn- set-to-memory
  [{mem :mem} k v]
  (swap! mem assoc k v))

(defn- get-from-memory
  [{mem :mem} k]
  (get @mem k))

(defrecord MemoryBrain [mem]
  component/Lifecycle
  (start [this]
    (if mem
      this
      (do (println ";; start memory brain")
          (let [this (assoc this :mem (atom {}))]
            (assoc this
                   :set (partial set-to-memory this)
                   :get (partial get-from-memory this))))))
  (stop [this]
    (if-not mem
      this
      (do (println ";; stop memory brain")
          (assoc this :mem nil :set nil :get nil)))))
