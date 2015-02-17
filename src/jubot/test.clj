(ns jubot.test
  "Jubot testing utilities"
  (:require
    [com.stuartsierra.component :as component]
    [jubot.system       :refer [system]]
    [jubot.brain.memory :refer :all]))

(defmacro with-test-brain
  "Wrap body with a test memory brain.
  
  Example
    (with-test-brain
      (jubot.brain/set \"foo\" \"bar\")
      (println (jubot.brain/get \"foo\"))) ; => \"bar\"
  "
  [& body]
  `(let [before# system
         brain# (map->MemoryBrain {})]
     (alter-var-root #'jubot.system/system (constantly {:brain (component/start brain#)}))
     (do ~@body)
     (alter-var-root #'jubot.system/system (constantly before#))))
