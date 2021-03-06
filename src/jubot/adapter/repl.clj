(ns jubot.adapter.repl
  "Jubot adapter for REPL."
  (:require
    [jubot.adapter.util :refer :all]
    [jubot.redef        :refer :all]
    [com.stuartsierra.component :as component]))

(def ^{:doc "User name. (default value is \"nobody\")"}
  username (or (getenv* "USER") "nobody"))

(defn process-output
  "Process output to REPL.

  Params
    this    - REPL adapter.
      :name - Bot's name.
    s       - Output text to REPL.
    option
      :as   - Overwrite bot's name if you specify this option.
  "
  [this s & {:keys [as] :as option}]
  (let [name (or as (:name this))]
    (println* (str name "=> " s))))

(defn process-input
  "Process input from REPL.

  Params
    this       - REPL adapter.
      :name    - Bot's name.
      :handler - A handler function.
    s          - Input text from REPL.
  "
  [{:keys [name handler] :as this} s]
  (let [option {:user username :channel nil}]
    (some->> s
             (parse-text name)
             (merge option)
             handler
             (process-output this))))

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
