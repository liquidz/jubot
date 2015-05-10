(ns jubot.brain.redis
  "Jubot brain for Redis."
  (:require
    [com.stuartsierra.component :as component]
    [jubot.redef          :refer :all]
    [taoensso.timbre      :as timbre]
    [taoensso.carmine     :as car]))

(def ^{:const true
       :doc "Default redis URI."}
  DEFAULT_REDIS_URI "redis://localhost:6379/")

(defn error* [e] (timbre/error e))

(defn- set-to-redis
  [conn k v]
  (try
    (car/wcar conn (car/set k v))
    (catch Exception e
      (error* e))))

(defn- get-from-redis
  [conn k]
  (try
    (car/wcar conn (car/get k))
    (catch Exception e
      (error* e))))

(defn- keys-from-redis
  [conn]
  (try
    (car/wcar conn (car/keys "*"))
    (catch Exception e
      (error* e))))

(defrecord RedisBrain [conn uri]
  component/Lifecycle
  (start [this]
    (if conn
      this
      (let [conn {:pool {}
                  :spec {:uri (or uri
                                  (getenv* "REDISCLOUD_URL")
                                  DEFAULT_REDIS_URI)}}]
        (println ";; start redis brain. redis url is" (-> conn :spec :uri))
        (assoc this
               :conn conn
               :set  (partial set-to-redis conn)
               :get  (partial get-from-redis conn)
               :keys (partial keys-from-redis conn)))))
  (stop [this]
    (if-not conn
      this
      (do (println ";; stop redis brain")
          (assoc this :conn nil :set nil :get nil)))))
