(ns jubot.adapter.repl
  (:require
    [jubot.adapter.util :refer :all]
    [com.stuartsierra.component :as component]))

(defn- process-output
  [{:keys [name]} s]
  (println (str name "=> " s)))

(defn- process-input
  [{:keys [name handler] :as this} s]
  (some->> s
           (text-to-bot name)
           handler
           (process-output this)))

(defrecord ReplAdapter [name handler in out]
  component/Lifecycle
  (start [this]
    (if (and in out)
      this
      (do (println ";; start repl adapter. bot name is" name)
          (assoc this
                 :in  (partial process-input this)
                 :out (partial process-output this)))))
  (stop [this]
    (if-not (and in out)
      this
      (do (println ";; stop repl adapter")
          (assoc this :in nil :out nil)))))
