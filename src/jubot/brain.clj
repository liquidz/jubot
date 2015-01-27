(ns jubot.brain
  (:require
    [taoensso.timbre  :as timbre]
    [taoensso.carmine :as car])
  (:refer-clojure :exclude [get set])
  (:import [java.net SocketException]))

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

(defn get
  [k]
  (wcar* (car/get k)))

(defn set
  [k v]
  (wcar* (car/set k v)))
