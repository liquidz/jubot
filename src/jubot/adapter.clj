(ns jubot.adapter
  "Jubot adapter manager."
  (:require
    [jubot.system :refer [system]]
    [jubot.adapter.repl :refer [map->ReplAdapter]]
    [jubot.adapter.slack :refer [map->SlackAdapter]]))

(defn create-adapter
  "Create the specified adapter.

  Params
    :adapter - a adapter name
  Return
    adapter component
  "
  [{:keys [adapter] :as config-option}]
  (case adapter
    "slack" (map->SlackAdapter config-option)
    (map->ReplAdapter config-option)))

(defn out
  "Call output function in system adapter.
  Before using this function, jubot.system should be started.

  Params
    s - a message string
  Return
    nil
  "
  [s]
  (some-> system :adapter :out (as-> f (f s))))
