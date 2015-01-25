(ns jubot.adapter.shell
  (:require
    [jubot.adapter :refer [Adapter]]))

(def username (or (System/getenv "USER") "anonymous"))

(defn output
  [{botname :botname} text]
  (println (str botname ": " text)))

(defn input
  [this handler-fn]
  (let [text (read-line)
        resp (if (not= text "")
               (handler-fn this text))]
    (if (string? resp)
      (output this (str username " " text)))))

(defrecord ShellAdapter [botname]
  Adapter
  (start! [this handler-fn]
    (println "jubot shell adapter started.")
    (.start (Thread. #(while true (input this handler-fn)))))
  (send! [this text]
    (output this text)))





