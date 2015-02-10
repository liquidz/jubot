(ns jubot.adapter-test
  (:require
    [jubot.adapter     :refer :all]
    [jubot.system      :refer [system]]
    [jubot.system-test :refer [with-mocked-system]]
    [clojure.test      :refer :all]
    [conjure.core      :refer [stubbing]]))

(def ^:private botname "test")


(deftest test-create-adapter
  (testing "slack adapter"
    (let [adapter (create-adapter {:name botname :adapter "slack"})]
      (are [x y] (= x y)
           botname (:name adapter)
           jubot.adapter.slack.SlackAdapter (type adapter))))

  (testing "default(repl) adapter"
    (let [adapter (create-adapter {:name botname})]
      (are [x y] (= x y)
           botname (:name adapter)
           jubot.adapter.repl.ReplAdapter (type adapter)))))

(deftest test-out
  (is (nil? (out "foo")))
  (with-mocked-system
    {:adapter {:out #(str "[" % "]")}}
    (fn []
      (is (= "[foo]" (out "foo"))))))
