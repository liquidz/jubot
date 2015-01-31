(ns jubot.util.handler-test
  (:require
    [jubot.util.handler :refer :all]
    [clojure.test :refer :all]))

(def ^:private regexp-handler*
  (partial
    (regexp-handler
      #"^ping$"      (constantly "pong")
      #"^this$"      (fn [this & _] this)
      #"^set (.+?)$" (fn [this [[_ x]]] (str "s:" x))
      #"^get (.+?)$" (fn [this [[_ x]]] (str "g:" x))
      :else          (constantly "error"))
    "adapter"))

(deftest test-regexp-handler
  (testing "should work fine"
    (are [x y] (= x (regexp-handler* y))
         "pong"    "ping"
         "adapter" "this"
         "s:foo"   "set foo"
         "g:bar"   "get bar"
         "error"   "foobar"))

  (testing "without else"
    (is (nil? ((regexp-handler #"^ping$" (constantly "pong")) "adapter" "text"))))

  (testing "empty reg-fn-list"
    (is (nil? ((regexp-handler) "adapter" "text"))))

  (testing "invalid reg-fn-list"
    (is (thrown? AssertionError ((regexp-handler :lonely) "adapter" "text")))))

