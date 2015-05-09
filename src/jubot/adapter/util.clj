(ns jubot.adapter.util
  "Utilities for jubot adapter."
  (:require [clojure.string :as str]))

(defn parse-text
  "Parse passed text and return a map.

  Params
    botname - Bot's name.
    text    - Message from user.
  Return
    Parsed map.
  "
  [botname text]
  (let [[head tail] (some-> text (str/split #"\s+" 2))
        fuzzy?      (nil? (->> head str (re-find #"[@:]")))
        name        (some->> head str (re-find #"^@?(.+?):?$") second)
        forme?      (= botname name)]
    (merge {:message-for-me? forme?}
           (if (and name (not (and (not forme?) fuzzy?)))
             {:to name :text tail}
             {:text text}))))
