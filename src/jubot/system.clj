(ns jubot.system
  "Jubot system manager."
  (:require
    [com.stuartsierra.component :as component]))

(def ^{:doc "Jubot system var."}
  system {})

(defn init
  "Initialize stuartsierra.component system.

  Params
    create-system-fn - A function to create jubot system.
  "
  [create-system-fn]
  (alter-var-root
    #'system
    (constantly (create-system-fn))))

(defn start
  "Start stuartsierra.component system.
  "
  []
  (alter-var-root #'system component/start))

(defn stop
  "Stop stuartsierra.component system.
  "
  []
  (alter-var-root
    #'system
    (fn [s] (when s (component/stop s)))))
