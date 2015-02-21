(ns jubot.handler-test
  (:require
    [jubot.handler  :as handler]
    [clojure.string :as str]
    [clojure.test   :refer :all]))

(def ^:private regexp-handler*
  (handler/regexp
    #"^ping$"      (constantly "pong")
    #"^opt$"       (fn [{:keys [user channel]}]
                     (str "u=" user ",c=" channel))
    #"^set (.+?)$" (fn [{[_ x] :match}] (str "s:" x))
    #"^get (.+?)$" (fn [{[_ x] :match}] (str "g:" x))
    :else          (constantly "error")))

(deftest test-regexp
  (testing "should work fine"
    (are [x y] (= x (regexp-handler* {:user "aa" :channel "bb" :text y}))
         "pong"      "ping"
         "u=aa,c=bb" "opt"
         "s:foo"     "set foo"
         "g:bar"     "get bar"
         "error"     "foobar"))

  (testing "nil input"
    (is (nil? (regexp-handler* {}))))

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

  (testing "empty list"
    (is (= identity (handler/comp))))

  (testing "invalid reg-fn-list"
    (is (thrown? AssertionError (handler/comp 'a 'b)))))

(deftest test-collect
  (do (create-ns 'jubot.test.handler.a)
      (create-ns 'jubot.test.handler.b)
      (intern 'jubot.test.handler.a 'a-handler
              (fn [{text :text}] (if (= "ping" text) "pong")))
      (intern 'jubot.test.handler.b 'b-handler
              (fn [{text :text}] (if (= "foo" text) "bar"))))

  (testing "public-handlers"
    (is (= (map resolve '(jubot.test.handler.a/a-handler
                          jubot.test.handler.b/b-handler))
           (sort #(.compareTo (-> %1 meta :name) (-> %2 meta :name))
                 (handler/public-handlers #"^jubot\.test\.handler"))))
    (is (= (map resolve '(jubot.test.handler.a/a-handler))
           (handler/public-handlers #"^jubot\.test\.handler\.a"))))

  (testing "collect"
    (are [x y] (= x ((handler/collect #"^jubot\.test\.handler") {:text y}))
         "pong" "ping"
         "bar"  "foo"
         nil    "xxx")
    (are [x y] (= x ((handler/collect #"^jubot\.test\.handler\.a") {:text y}))
         "pong" "ping"
         nil    "foo"
         nil    "xxx")
    (is (nil? ((handler/collect #"^foobar") {:text "foo"})))))

(deftest test-help-handler-fn
  (do (create-ns 'jubot.test.help-handler)
      (intern 'jubot.test.help-handler
              (with-meta 'ping-handler {:doc "pingpong handler"})
              (constantly nil))
      (intern 'jubot.test.help-handler
              (with-meta 'foo-handler {:doc "foobar handler"})
              (constantly nil)))

  (let [f     (handler/help-handler-fn #"^jubot\.test\.help-handler")
        helps (str/split-lines (f {:text "help"}))]
    (is (seq (filter #(= % "pingpong handler") helps)))
    (is (seq (filter #(= % "foobar handler") helps)))))
