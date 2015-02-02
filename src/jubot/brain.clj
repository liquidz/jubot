(ns jubot.brain
  (:refer-clojure :exclude [set get]))

(def ^:private brain (atom nil))

(defprotocol Brain
  (set* [this k v])
  (get* [this k]))

(defn set-brain!
  [b]
  (reset! brain b))

(defn set
  [k v]
  (set* @brain k v))

(defn get
  [k]
  (get* @brain k))
