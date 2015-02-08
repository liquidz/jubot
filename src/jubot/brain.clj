(ns jubot.brain
  (:require
    [jubot.system       :refer [system]]
    [jubot.brain.memory :refer [map->MemoryBrain]]
    [jubot.brain.redis  :refer [map->RedisBrain]])
  (:refer-clojure :exclude [set get]))

(defn create-brain
  [{:keys [brain] :as config-option}]
  (case brain
    "redis" (map->RedisBrain config-option)
    (map->MemoryBrain config-option)))

(defn set
  [k v]
  (some-> system :brain :set (as-> f (f k v))))

(defn get
  [k]
  (some-> system :brain :get (as-> f (f k))))
