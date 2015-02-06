(ns jubot.adapter.test
  (:require
    [jubot.adapter :refer :all]))

(defadapter TestAdapter
  (start*
    [this handler-fn]
    (str "started: " (:botname this)))
  (send*
    [this text]
    (str "send: " (:botname this) " " text)))





