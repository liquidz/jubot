(ns jubot.schedule
  (:require [cronj.core :as c]))

(defn schedule
  [cron-expr f]
  (with-meta f {:schedule cron-expr}))

(defn schedules
  [& args]
  (map #(apply schedule %) (partition 2 args)))

(defn- schedule->task
  [f]
  {:id       (str (gensym "task"))
   :handler  (fn [t opt] (f))
   :schedule (-> f meta :schedule)})

(defn start-schedule!
  [adapter entries]
  (when (seq entries)
    (c/start!
      (c/cronj :entries (map schedule->task entries)))))
