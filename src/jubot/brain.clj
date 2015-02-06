(ns jubot.brain
  (:refer-clojure :exclude [set get]))

(def ^:private _brain_ (atom nil))

(defprotocol Brain
  (set* [this k v])
  (get* [this k]))

(defn set-brain!
  [b]
  (reset! _brain_ b))

(defn set
  [k v]
  (set* @_brain_ k v))

(defn get
  [k]
  (get* @_brain_ k))
