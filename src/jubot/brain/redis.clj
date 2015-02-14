(ns jubot.brain.redis
  (:require
    [com.stuartsierra.component :as component]
    [jubot.di             :refer :all]
    [taoensso.timbre      :as timbre]
    [taoensso.carmine     :as car]))

(def ^:const DEFAULT_REDIS_URI "redis://localhost:6379/")

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
      (let [conn {:pool {}
                  :uri (or uri
                           (getenv* "REDISCLOUD_URL")
                           DEFAULT_REDIS_URI)}]
        (println ";; start redis brain. redis url is" (:uri conn))
        (assoc this
               :conn conn
               :set (partial set-to-redis conn)
               :get (partial get-from-redis conn)))))
  (stop [this]
    (if-not conn
      this
      (do (println ";; stop redis brain")
          (assoc this :conn nil :set nil :get nil)))))
