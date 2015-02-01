(ns jubot.core
  (:require
    [clojure.tools.cli   :refer [parse-opts]]
    [jubot.adapter       :refer :all]
    [jubot.brain         :as    brain]
    [jubot.schedule      :refer [set-schedule! start-schedule!]]
    [jubot.adapter.shell :refer [->ShellAdapter]]
    [jubot.adapter.slack :refer [->SlackAdapter]]
    [jubot.util.handler  :refer :all]))

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

(def handler
  (regexp-handler
    #"^ping$"            (constantly "pong")
    #"^set (.+?) (.+?)$" (fn [this [[_ k v]]] (brain/set k v))
    #"^get (.+?)$"       (fn [this [[_ k]]]   (brain/get k))
    #"^out (.+?)$"       (fn [this [[_ s]]]
                           (send! this (str "uochan kiteru:" s))
                           "done")))

(set-schedule!
  "0 0 * * * * *"
  (fn [adapter]
    (send! adapter "KITERU")))

(def -main (jubot handler))
