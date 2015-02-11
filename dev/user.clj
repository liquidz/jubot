(ns user
  (:require
    [com.stuartsierra.component :as component]
    [clojure.tools.namespace.repl :refer [refresh]]
    ;[jubot.component :as app]
    [jubot.core :as core]
    [jubot.system :as sys]

    ;; FIXME
    jubot.adapter
    jubot.brain
    jubot.util.handler
    jubot.scheduler
    ))

(def test-handler
  (jubot.util.handler/regexp-handler
    #"^ping$"            (constantly "PONG")
    #"^option$"          (fn [opt] (str opt))
    #"^set (.+?) (.+?)$" (fn [{[[_ k v]] :match}] (jubot.brain/set k v))
    #"^get (.+?)$"       (fn [{[[_ k]] :match}]   (jubot.brain/get k))
    :else                (constantly "unknown command")))

(def test-schedule
  []
  #_(jubot.scheduler/schedules
    "/5 * * * * * *" #(jubot.adapter/out "kiteruyo!!"))
  )

(def ^:private create-system
  (core/create-system-fn :name "jubot"
                         :handler test-handler
                         :entries test-schedule
                         ))

(def init (partial sys/init #(create-system {:adapter "repl"
                                             :brain "memory"})))


(def start sys/start)
(def stop sys/stop)

(defn go []
  (init)
  (sys/start))

(defn reset []
  (sys/stop)
  (refresh :after 'user/go))

(defn in [s]
  ((-> sys/system :adapter :in) s))
