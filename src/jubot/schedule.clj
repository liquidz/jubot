(ns jubot.schedule
  (:require [cronj.core :as c]))

(def ^:private cron-entries (atom []))

(defn clear-schedule!
  []
  (reset! cron-entries []))

(defn set-schedule!
  [cron-expr f]
  (swap! cron-entries conj (with-meta f {:schedule cron-expr})))

(defn- schedule->task
  [adapter f]
  {:id       (str (gensym "task"))
   :handler  #(-> %2 :adapter f)
   :schedule (-> f meta :schedule)
   :opts     {:adapter adapter}})

(defn start-schedule!
  [adapter]
  (when-not (empty? @cron-entries)
    (c/start!
      (c/cronj :entries (map (partial schedule->task adapter)
                             @cron-entries)))))


