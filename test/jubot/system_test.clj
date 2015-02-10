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
