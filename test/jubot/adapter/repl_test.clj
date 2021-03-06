(ns jubot.adapter.repl-test
  (:require
    [com.stuartsierra.component :as component]
    [jubot.adapter.repl :refer :all]
    [jubot.redef        :refer :all]
    [conjure.core       :refer [stubbing]]
    [clojure.test       :refer :all]))

(def ^:private botname "test")
(def ^:private handler (fn [{:keys [user channel text message-for-me?]}]
                         (when message-for-me?
                           (str "user=" user ",channel=" channel ",text=" text))))
(def ^:private adapter (map->ReplAdapter {:name botname :handler handler}))
(def ^:private process-output* (partial process-output adapter))
(def ^:private process-input*  (partial process-input adapter))

(deftest test-process-output
  (stubbing [println* identity]
    (are [x y] (= x y)
      "test=> foo" (process-output* "foo")
      "bar=> foo"  (process-output* "foo" :as "bar"))))

(deftest test-process-input
  (stubbing [println* identity]
    (is (nil? (process-input* "foo")))
    (is (= (str botname "=> user=" username ",channel=,text=foo")
           (process-input* (str botname " foo"))))))

(deftest test-ReplAdapter
  (testing "start adapter"
    (let [adapter (component/start adapter)]
      (is (fn? (:in  adapter)))
      (is (fn? (:out adapter)))))

  (testing "stop adapter"
    (let [adapter (-> adapter component/start component/stop)]
      (is (nil? (:in  adapter)))
      (is (nil? (:out adapter))))))
