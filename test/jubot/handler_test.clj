(ns jubot.handler-test
  (:require
    [jubot.handler :as handler]
    [clojure.test  :refer :all]))

(def ^:private regexp-handler*
  (handler/regexp
    #"^ping$"      (constantly "pong")
    #"^opt$"       (fn [{:keys [user channel]}]
                     (str "u=" user ",c=" channel))
    #"^set (.+?)$" (fn [{[[_ x]] :match}] (str "s:" x))
    #"^get (.+?)$" (fn [{[[_ x]] :match}] (str "g:" x))
    :else          (constantly "error")))

(deftest test-regexp
  (testing "should work fine"
    (are [x y] (= x (regexp-handler* {:user "aa" :channel "bb" :text y}))
         "pong"      "ping"
         "u=aa,c=bb" "opt"
         "s:foo"     "set foo"
         "g:bar"     "get bar"
         "error"     "foobar"))

  (testing "without else"
    (is (nil? ((handler/regexp #"^ping$" (constantly "pong")) {:text "text"}))))

  (testing "empty reg-fn-list"
    (is (nil? ((handler/regexp) {:text "text"}))))

  (testing "invalid reg-fn-list"
    (is (thrown? AssertionError ((handler/regexp :lonely) {:text "text"})))))

(deftest test-comp
  (testing "handler composition"
    (let [f (fn [{text :text}]
              (case text "aaa" "111", "bbb" "222", "ping" "pong" nil))
          g (fn [{text :text}]
              (case text "bbb" "333", "aaa" "444", "foo" "bar" nil))]

      (are [x y] (= x ((handler/comp g f) {:text y}))
           "111"  "aaa"
           "222"  "bbb"
           "pong" "ping"
           "bar"  "foo"
           nil    "xxx")

      (are [x y] (= x ((handler/comp f g) {:text y}))
           "444"  "aaa"
           "333"  "bbb"
           "pong" "ping"
           "bar"  "foo"
           nil    "xxx")))

  (testing "invalid reg-fn-list"
    (is (thrown? AssertionError (handler/comp 'a 'b)))))
