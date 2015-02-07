(ns jubot.adapter.util-test
  (:require
    [jubot.adapter.util :refer :all]
    [clojure.test :refer :all]
    )
  )

(deftest test-text-to-bot
  (are [x y] (= x (text-to-bot "jubot" y))
       nil      "foo bar"
       nil      "jubot"
       ""       "jubot "
       "hello"  "jubot hello"))
