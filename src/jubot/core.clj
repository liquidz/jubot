(ns jubot.core
  (:require
    [clojure.tools.cli   :refer [parse-opts]]
    [jubot.adapter       :refer :all]
    [jubot.brain         :refer [set-brain!]]
    [jubot.brain.memory  :refer [->MemoryBrain]]
    [jubot.brain.redis   :refer [->RedisBrain]]
    [jubot.schedule      :refer [start-schedule!]]
    [jubot.adapter.shell :refer [->ShellAdapter]]
    [jubot.adapter.slack :refer [->SlackAdapter]]
    ))

(def ^:private DEFAULT_ADAPTER "shell")
(def ^:private DEFAULT_BRAIN   "memory")
(def ^:private DEFAULT_BOTNAME "jubot")

(def ^:private cli-options
  [["-a" "--adapter ADAPTER_NAME" "Select adapter" :default DEFAULT_ADAPTER]
   ["-b" "--brain NAME"           "Select brain"   :default DEFAULT_BRAIN]
   ["-n" "--name NAME"            "Set bot name"   :default DEFAULT_BOTNAME] ])

(defn jubot
  [& {:keys [handler schedule]}]
  (fn [& args]
    (let [{:keys [options _ _ errors]} (parse-opts args cli-options)
          botname (:name options)
          adapter (case (:adapter options)
                    "slack" (->SlackAdapter botname)
                    (->ShellAdapter botname))
          brain   (case (:brain options)
                    "redis" (->RedisBrain)
                    (->MemoryBrain))]
      (set-brain! brain)
      (start-schedule! adapter schedule)
      (start-adapter! adapter handler))))
