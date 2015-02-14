(ns jubot.scheduler
  (:require
    [com.stuartsierra.component :as component]
    [cronj.core :as c]))

(def ^:const SCHEDULE_REGEXP #"^.*-schedule$")

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

(defrecord Scheduler [cj entries]
  component/Lifecycle
  (start [this]
    (if cj
      this
      (do (println ";; start scheduler")
          (let [cj (c/cronj :entries (map schedule->task entries))]
            (c/start! cj)
            (assoc this :cj cj)))))

  (stop [this]
    (if-not cj
      this
      (do (println ";; stop scheduler")
          (c/stop! cj)
          (assoc this :cj nil)))))

(defn create-scheduler
  [config-option]
  (map->Scheduler (merge {:entries []}
                         config-option)))

(defn public-schedules
  [ns-regexp]
  (->> (all-ns)
       (filter #(re-find ns-regexp (str (ns-name %))))
       (mapcat #(vals (ns-publics %)))
       (filter #(re-matches SCHEDULE_REGEXP (-> % meta :name str)))))

(defn collect
  [ns-regexp]
  (flatten (map var-get (public-schedules ns-regexp))))
