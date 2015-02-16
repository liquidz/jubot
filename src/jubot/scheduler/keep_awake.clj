(ns jubot.scheduler.keep-awake
  "Built-in schedule to keep awake jubot system on PaaS. (such as Heroku)"
  (:require
    [jubot.redef          :refer :all]
    [jubot.scheduler      :as js]
    [clj-http.lite.client :as client]))

(def ^{:const true
       :doc "Interval to awake system."}
  AWAKE_INTERVAL "/30 * * * * * *")

(def ^{:const true
       :doc "Key of environment variables which handle url to awake system."}
  AWAKE_URL_KEY "AWAKE_URL")

(def ^{:doc "Schedule to keep awake system."}
  keep-awake-schedule
  (js/schedule
    AWAKE_INTERVAL #(some-> AWAKE_URL_KEY getenv* client/get)))

