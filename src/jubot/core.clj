(ns jubot.core
  "Jubot core"
  (:require
    [com.stuartsierra.component :as component]
    [clojure.tools.cli :refer [parse-opts]]
    [jubot.system      :as sys]
    [jubot.adapter     :as ja]
    [jubot.brain       :as jb]
    [jubot.scheduler   :as js]
    [jubot.handler     :as jh]
    [jubot.require     :as jr]
    [jubot.scheduler [keep-awake]]))

(defn create-system-fn
  "Returns function to create stuartsierra.component system.

  Params
    :name    - a bot's name
    :handler - a handler function
    :entries - a sequence of schedule entries

  Return
    (fn [{:keys [adapter brain] :as config-option}])
  "
  [& {:keys [name handler entries] :or {entries []}}]
  (fn [{:keys [adapter brain] :as config-option}]
    (let [built-in-entries (js/collect #"^jubot.scheduler\.")
          entries          (concat entries built-in-entries)]
      (component/system-map
        :adapter   (ja/create-adapter   {:adapter adapter
                                         :name name
                                         :handler handler})
        :brain     (jb/create-brain     {:brain brain})
        :scheduler (js/create-scheduler {:entries entries})))))

(def ^{:const true :doc "Default adapter's name"} DEFAULT_ADAPTER "slack")
(def ^{:const true :doc "Default brain's name"}   DEFAULT_BRAIN   "memory")
(def ^{:const true :doc "Default bot's name"}     DEFAULT_BOTNAME "jubot")

(def ^:private cli-options
  [["-a" "--adapter ADAPTER_NAME" "Select adapter" :default DEFAULT_ADAPTER]
   ["-b" "--brain NAME"           "Select brain"   :default DEFAULT_BRAIN]
   ["-n" "--name NAME"            "Set bot name"   :default DEFAULT_BOTNAME]])

(defn jubot
  "Returns jubot -main function.

  Params
    :handler   - a handler function
    :entries   - a sequence of schedule entries
    :ns-regexp - a regular-expression which specifies bot's namespace.
                 if a handler and entries are omitted,
                 these are collected automatically from the specified namespace.

  Return
    (fn [& args])
  "
  [& {:keys [handler entries ns-regexp]}]
  (fn [& args]
    ; require namespaces automatically
    (when ns-regexp (jr/regexp-require ns-regexp))
    (let [{:keys [options _ _ errors]} (parse-opts args cli-options)
          {:keys [name adapter brain debug]} options
          handler (or handler (and ns-regexp (jh/collect ns-regexp)))
          entries (or entries (and ns-regexp (js/collect ns-regexp)))
          create-system (create-system-fn
                          :name    name
                          :handler handler
                          :entries entries)]
      (sys/init #(create-system options))
      (sys/start))))
