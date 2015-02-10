(ns jubot.system-test
  (:require
    [com.stuartsierra.component :as component]
    [jubot.system :refer :all]
    [clojure.test :refer :all]))

(defn with-mocked-system
  [system-value f]
  (let [before system]
    (alter-var-root #'jubot.system/system (constantly system-value))
    (f)
    (alter-var-root #'jubot.system/system (constantly before))))

(defrecord TestComponent [foo]
  component/Lifecycle
  (start [this] (assoc this :foo "bar"))
  (stop [this] (assoc this :foo nil)))

(defn- create-test-system
  []
  (component/system-map
    :test (map->TestComponent {})))

(deftest test-init
  (init (fn [] "foo"))
  (is (= "foo" system)))

(deftest test-start
  (do (init create-test-system)
      (start))
  (is (= "bar" (-> system :test :foo))))

(deftest test-stop
  (do (init create-test-system)
      (start)
      (stop))
  (is (nil? (-> system :test :foo))))
