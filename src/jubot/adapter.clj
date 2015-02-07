(ns jubot.adapter
  (:require [jubot.adapter.protocol :refer :all]))

(def ^:private _adapter_ (atom nil))

(defn set-adapter!
  [a]
  (reset! _adapter_ a))

(defn send!
  [text]
  (send* @_adapter_ text))

(defn start-adapter!
  [adapter handler-fn]
  (set-adapter! adapter)
  (start* @_adapter_ handler-fn))

