(ns jubot.adapter.shell
  (:require
    [jubot.adapter :refer :all]))

(def username (or (System/getenv "USER") "anonymous"))

(def println* #(apply println %&))

(defn process-output
  [{:keys [botname]} text]
  (println* botname "=>" text))

(defn process-input
  [this handler-fn text]
  (some->> text
           (text-to-bot (:botname this))
           handler-fn
           (process-output this)))

(defadapter ShellAdapter
  (start* [this handler-fn]
          (println* "this is jubot shell adapter.")
          (println* (str "bot's name is \"" (:botname this) "\"."))
          (.start (Thread.  #(while true
                               (process-input this handler-fn (read-line))))))
  (send* [this text]
         (process-output this text)))





