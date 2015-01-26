(ns jubot.adapter.shell
  (:require
    [clojure.string :as str]
    [jubot.adapter :refer :all]))

(def username (or (System/getenv "USER") "anonymous"))

(defn output
  [{:keys [botname]} text]
  (println botname "=>" text))

(defn input
  [this handler-fn]
  (let [this (assoc this :from username)]
    (some->> (read-line)
             (text-to-bot (:botname this))
             (handler-fn this)
             (output this))))

(defrecord ShellAdapter [botname]
  Adapter
  (start! [this handler-fn]
    (println "jubot shell adapter started.")
    (.start (Thread. #(while true (input this handler-fn)))))
  (send! [this text]
    (output this text)))





