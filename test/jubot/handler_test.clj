(ns jubot.handler-test
  (:require
    [jubot.handler  :as handler]
    [clojure.string :as str]
    [clojure.test   :refer :all]))

(defn- regexp-handler*
  [arg]
  (handler/regexp
    arg
    #"^ping$"      (constantly "pong")
    #"^opt$"       (fn [{:keys [user channel]}]
                     (str "u=" user ",c=" channel))
    #"^set (.+?)$" (fn [{[_ x] :match}] (str "s:" x))
    #"^get (.+?)$" (fn [{[_ x] :match}] (str "g:" x))
    :else          (constantly "error")))

(defn test-ns-fixture
  [f]
  (create-ns 'jubot.test.handler.a)
  (create-ns 'jubot.test.handler.b)
  (create-ns 'jubot.test.handler.b-test)
  (intern 'jubot.test.handler.a
          (with-meta 'a-handler {:doc "pingpong-help
                                       pingpong-handler"})
          (fn [{text :text}] (if (= "ping" text) "pong")))
  (intern 'jubot.test.handler.b
          (with-meta 'b-handler {:doc "foobar-help
                                       foobar-handler"})
          (fn [{text :text}] (if (= "foo" text) "bar")))
  (intern 'jubot.test.handler.b
          'no-doc-handler
          (fn [{text :text}] (if (= "bar" text) "baz")))
  (intern 'jubot.test.handler.b-test 'must-not-be-collected-handler
          (constantly nil))
  (f)
  (remove-ns 'jubot.test.handler.a)
  (remove-ns 'jubot.test.handler.b)
  (remove-ns 'jubot.test.handler.b-test))

(use-fixtures :each test-ns-fixture)

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
    (is (nil? (handler/regexp {:text "text"} #"^ping$" (constantly "pong")))))

  (testing "empty reg-fn-list"
    (is (nil? (handler/regexp {:text "text"}))))

  (testing "invalid reg-fn-list"
    (is (thrown? AssertionError (handler/regexp {:text "text"} :lonely)))))

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

(deftest test-public-handlers
  (is (= (map resolve '(jubot.test.handler.a/a-handler
                        jubot.test.handler.b/b-handler
                        jubot.test.handler.b/no-doc-handler))
         (sort #(.compareTo (-> %1 meta :name) (-> %2 meta :name))
               (handler/public-handlers #"^jubot\.test\.handler"))))
  (is (= (map resolve '(jubot.test.handler.a/a-handler))
         (handler/public-handlers #"^jubot\.test\.handler\.a"))))

(deftest test-collect
  (are [x y] (= x ((handler/collect #"^jubot\.test\.handler") {:text y}))
       "pong" "ping"
       "bar"  "foo"
       nil    "xxx")
  (are [x y] (= x ((handler/collect #"^jubot\.test\.handler\.a") {:text y}))
       "pong" "ping"
       nil    "foo"
       nil    "xxx")
  (is (nil? ((handler/collect #"^foobar") {:text "foo"}))))

(deftest test-help-handler-fn
  (let [f     (handler/help-handler-fn #"^jubot\.test\.handler")
        helps (str/split-lines (f {:message-for-me? true :text "help"}))]
    (are [x] (seq (filter #(= % x) helps))
         "pingpong-help"
         "pingpong-handler"
         "foobar-help"
         "foobar-handler")
    (is (empty? (filter #(= % "") helps)))))
