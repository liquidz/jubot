(ns jubot.core
  (:require
    [clojure.tools.cli   :refer [parse-opts]]
    [jubot.adapter       :refer :all]
    [jubot.schedule      :refer [start-schedule!]]
    [jubot.adapter.shell :refer [->ShellAdapter]]
    [jubot.adapter.slack :refer [->SlackAdapter]]))

(def ^:private DEFAULT_ADAPTER "shell")
(def ^:private DEFAULT_BOTNAME "jubot")

(def ^:private cli-options
  [["-a" "--adapter ADAPTER_NAME" "Select adapter" :default DEFAULT_ADAPTER]
   ["-n" "--name NAME"            "Set bot name"   :default DEFAULT_BOTNAME]])

(defn jubot
  [handler-fn]
  (fn [& args]
    (let [{:keys [options _ _ errors]} (parse-opts args cli-options)
          botname (:name options)
          adapter (case (:adapter options)
                    "slack" (->SlackAdapter botname)
                    (->ShellAdapter botname))]
      (start-schedule! adapter)
      (start-adapter adapter handler-fn))))
