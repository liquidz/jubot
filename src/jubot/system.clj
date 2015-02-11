(ns jubot.system
  (:require
    [com.stuartsierra.component :as component]))

(def system {})

(defn init
  [create-system-fn]
  (alter-var-root
    #'system
    (constantly (create-system-fn))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root
    #'system
    (fn [s] (when s (component/stop s)))))
