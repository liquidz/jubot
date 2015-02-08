(ns jubot.brain.redis
  (:require
    [com.stuartsierra.component :as component]
    [taoensso.timbre      :as timbre]
    [taoensso.carmine     :as car]))

(def ^:private DEFAULT_REDIS_URI "redis://localhost:6379/")

(defn- set-to-redis
  [conn k v]
  (try
    (car/wcar conn (car/set k v))
    (catch Exception e
      (timbre/error e))))

(defn- get-from-redis
  [conn k]
  (try
    (car/wcar conn (car/get k))
    (catch Exception e
      (timbre/error e))))

(defrecord RedisBrain [conn uri]
  component/Lifecycle
  (start [this]
    (if conn
      this
      (do (println ";; start redis brain")
          (let [conn {:pool {}
                      :uri (or uri
                               (System/getenv "REDISCLOUD_URL")
                               DEFAULT_REDIS_URI)}]
            (assoc this
                   :conn conn
                   :set (partial set-to-redis conn)
                   :get (partial get-from-redis conn))))))
  (stop [this]
    (if-not conn
      this
      (do (println ";; stop redis brain")
          (assoc this :conn nil)))))
