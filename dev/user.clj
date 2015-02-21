(ns user
  (:require
    [clojure.tools.namespace.repl :refer [refresh]]
    [clojure.repl :refer :all]
    [jubot.system :as sys]
    [dev          :refer [-main]]))

(def stop sys/stop)

(defn start []
  (-main "-a" "repl" "-b" "memory"))

(defn restart []
  (sys/stop)
  (refresh :after 'user/start))

(defn in [s]
  ((-> sys/system :adapter :in) s))
