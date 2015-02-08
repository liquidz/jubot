(ns jubot.core
  (:require
    [com.stuartsierra.component :as component]
    [jubot.system      :as sys]
    [jubot.adapter     :refer [create-adapter]]
    [jubot.brain       :refer [create-brain]]
    [jubot.scheduler   :refer [create-scheduler]]
    [clojure.tools.cli :refer [parse-opts]]))

(defn create-system-fn
  [& {:keys [name handler entries] :or {entries []}}]
  (fn [{:keys [adapter brain] :as config-option}]
    (component/system-map
      :adapter (create-adapter {:adapter adapter
                                :name name
                                :handler handler})
      :brain   (create-brain {:brain brain})
      :scheduler (create-scheduler {:entries entries}))))

(def ^:const DEFAULT_ADAPTER "slack")
(def ^:const DEFAULT_BRAIN   "memory")
(def ^:const DEFAULT_BOTNAME "jubot")

(def ^:private cli-options
  [["-a" "--adapter ADAPTER_NAME" "Select adapter" :default DEFAULT_ADAPTER]
   ["-b" "--brain NAME"           "Select brain"   :default DEFAULT_BRAIN]
   ["-n" "--name NAME"            "Set bot name"   :default DEFAULT_BOTNAME] ])

(defn jubot
  [& {:keys [handler entries]}]
  (fn [& args]
    (let [{:keys [options _ _ errors]} (parse-opts args cli-options)
          {:keys [name adapter brain]} options
          create-system (create-system-fn
                          :name name
                          :handler handler
                          :entries entries)]
      (sys/init #(create-system options))
      (sys/start))))

