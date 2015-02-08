(ns jubot.adapter
  (:require
    [jubot.system :refer [system]]
    [jubot.adapter.repl :refer [map->ReplAdapter]]
    [jubot.adapter.slack :refer [map->SlackAdapter]]
    ))

(defn create-adapter
  [{:keys [adapter] :as config-option}]
  (case adapter
    "slack" (map->SlackAdapter config-option)
    (map->ReplAdapter config-option)))

(defn out
  [s]
  (some-> system :adapter :out (as-> f (f s))))
