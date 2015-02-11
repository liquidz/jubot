(ns jubot.handler-test
  (:require
    [jubot.util.handler :refer :all]
    [clojure.test :refer :all]))

(def ^:private regexp-handler*
  (regexp-handler
    #"^ping$"      (constantly "pong")
    #"^opt$"       (fn [{:keys [user channel]}]
                     (str "u=" user ",c=" channel))
    #"^set (.+?)$" (fn [{[[_ x]] :match}] (str "s:" x))
    #"^get (.+?)$" (fn [{[[_ x]] :match}] (str "g:" x))
    :else          (constantly "error")))

(deftest test-regexp-handler
  (testing "should work fine"
    (are [x y] (= x (regexp-handler* {:user "aa" :channel "bb" :text y}))
         "pong"      "ping"
         "u=aa,c=bb" "opt"
         "s:foo"     "set foo"
         "g:bar"     "get bar"
         "error"     "foobar"))

  (testing "without else"
    (is (nil? ((regexp-handler #"^ping$" (constantly "pong")) {:text "text"}))))

  (testing "empty reg-fn-list"
    (is (nil? ((regexp-handler) {:text "text"}))))

  (testing "invalid reg-fn-list"
    (is (thrown? AssertionError ((regexp-handler :lonely) {:text "text"})))))
