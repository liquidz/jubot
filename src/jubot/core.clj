(ns jubot.core
  (:require
    [clojure.tools.cli :refer [parse-opts]]
    [jubot.adapter :refer :all]
    [jubot.brain :as brain]
    [jubot.adapter.shell :refer [->ShellAdapter]]
    [jubot.adapter.slack :refer [->SlackAdapter]]))

(def ^:private DEFAULT_ADAPTER "shell")
(def ^:private DEFAULT_BOTNAME "jubot")

(defn handler
  [this text]
  (str "hello " text)
  ;(let [x (str "hello " (brain/get "foo") " " text)]
  ;  (brain/set "foo" text)
  ;  x
  ;  )
  )

(def ^:private cli-options
  [["-a" "--adapter ADAPTER_NAME" "Select adapter" :default DEFAULT_ADAPTER]
   ["-n" "--name NAME"            "Set bot name"   :default DEFAULT_BOTNAME]
   ])

(defn jubot
  [handler-fn & args]
  (let [{:keys [options _ _ errors]} (parse-opts args cli-options)
        botname (:name options)
        adapter (case (:adapter options)
                  "slack" (->SlackAdapter botname)
                  (->ShellAdapter botname))]
    (start-adapter adapter handler-fn)))

(def -main (partial jubot handler))

;(defn -main
;  []
;  ;(start-adapter (->ShellAdapter "p") handler)
;  (start-adapter (->SlackAdapter "p") handler)
;  )

;(defn greeting
;  []
;  "Hello, world")
;
;(defroutes app
;  (GET "/" [] (greeting))
;  (not-found "page not found"))
