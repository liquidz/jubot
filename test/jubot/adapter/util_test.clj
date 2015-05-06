(ns jubot.adapter.util-test
  (:require
    [jubot.adapter.util :refer :all]
    [clojure.test       :refer :all]))

(deftest test-parse-text
  (are [x y] (= x (parse-text "foo" y))
       {:to "foo" :text "bar" :message-for-me? true}  "foo   bar"
       {:to "foo" :text "bar" :message-for-me? true}  "foo:  bar"
       {:to "foo" :text "bar" :message-for-me? true}  "@foo  bar"
       {:to "foo" :text "bar" :message-for-me? true}  "@foo: bar"

       {:text "baz   bar" :message-for-me? false}     "baz   bar"
       {:to "baz" :text "bar" :message-for-me? false} "baz:  bar"
       {:to "baz" :text "bar" :message-for-me? false} "@baz  bar"
       {:to "baz" :text "bar" :message-for-me? false} "@baz: bar"))
