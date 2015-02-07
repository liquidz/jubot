(ns jubot.adapter.util
  (:require [clojure.string :as str]))

(defn text-to-bot
  "ボットへのメッセージであれば、それを抜き出して返す"
  [botname text]
  (let [prefix (str botname " ")
        len    (count prefix)]
    (if (and (string? text) (.startsWith text prefix))
      (str/join (drop len text)))))
