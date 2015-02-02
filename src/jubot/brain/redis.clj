(ns jubot.brain.redis
  (:require
    [taoensso.timbre  :as timbre]
    [taoensso.carmine :as car]))

(def ^:private DEFAULT_REDIS_URI "redis://localhost:6379/")

(def ^:private conn
  {:pool {}
   :spec {:uri (or (System/getenv "REDISCLOUD_URL")
                   DEFAULT_REDIS_URI)}})

(defmacro wcar*
  [& body]
  `(try
     (car/wcar conn ~@body)
     (catch Exception ~'e
       (timbre/error ~'e))))

(defrecord RedisBrain []
  jubot.brain.Brain
  (set* [this k v] (wcar* (car/set k v)))
  (get* [this k]   (wcar* (car/get k))))
