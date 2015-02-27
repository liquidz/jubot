(ns jubot.scheduler
  "Jubot scheduler."
  (:require
    [com.stuartsierra.component :as component]
    [jubot.adapter :as ja]
    [cronj.core    :as c]))

(def ^{:const true
       :doc "The regular expression for collecting schedules automatically."}
  SCHEDULE_REGEXP #"^.*-schedule$")

(defn schedule
  "Generate a schedule from a pair of cronj format string and function.

  Params
    cron-expr - Cronj format string. http://docs.caudate.me/cronj/#crontab
    f         - A function with zero parameter.
  Return
    A schedule function.
  "
  [cron-expr f]
  (with-meta f {:schedule cron-expr}))

(defn schedules
  "Generate sequence of schedules from pairs of cronj format string and function.

  Params
    args - Pairs of cronj format string and function.
  Return
    Sequence of schedule functions.
  "
  [& args]
  (map #(apply schedule %) (partition 2 args)))

(defn schedule->task
  "Convert a schedule function to cronj task.

  Params
    f - A schedule function.
  Return
    A cronj task.
  "
  [f]
  {:id       (str (gensym "task"))
   :handler  (fn [t opt] (let [ret (f)]
                           (if (string? ret) (ja/out ret))))
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
  "Create the scheduler.

  Params
    :entries - Sequence of schedules.
  Return
    Scheduler component.
  "
  [{:keys [entries] :as config-option}]
  (map->Scheduler (merge {:entries []}
                         config-option)))

(defn public-schedules
  "Return sequence of public schedule vars which matched SCHEDULE_REGEXP in specified namespaces.

  Params
    ns-regexp - A regular expression which specifies namespaces for searching schedules.
  Return
    Sequence of schedule vars.
  "
  [ns-regexp]
  (->> (all-ns)
       (remove #(re-find #"-test$" (str (ns-name %))))
       (filter #(re-find ns-regexp (str (ns-name %))))
       (mapcat #(vals (ns-publics %)))
       (filter #(re-matches SCHEDULE_REGEXP (-> % meta :name str)))))

(defn collect
  "Return sequence of public schedules in specified namespaces.

  Params
    ns-regexp - A regular expression which specifies namespaces for searching schedules.
  Return
    Sequence of schedules.
  "
  [ns-regexp]
  (flatten (map var-get (public-schedules ns-regexp))))
