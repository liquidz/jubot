(ns jubot.adapter.util
  "Utilities for jubot adapter."
  (:require [clojure.string :as str]))

(defn text-to-bot
  "If the specified text starts with a bot's name, return text excepting bot's name.
  If that's not the case, return nil.

  Params
    botname - Bot's name.
    text    - Message from user.
  Return
    Text string or nil.
  "
  [botname text]
  (let [prefix (str botname " ")
        len    (count prefix)]
    (if (and (string? text) (.startsWith text prefix))
      (str/join (drop len text)))))
