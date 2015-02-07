(ns jubot.brain
  (:require [jubot.brain.protocol :refer :all])
  (:refer-clojure :exclude [set get]))

(def ^:private _brain_ (atom nil))

(defn set-brain!
  [b]
  (reset! _brain_ b))

(defn set
  [k v]
  (set* @_brain_ k v))

(defn get
  [k]
  (get* @_brain_ k))
