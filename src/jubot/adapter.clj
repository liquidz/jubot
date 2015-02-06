(ns jubot.adapter)

(def ^:private _adapter_ (atom nil))

(defprotocol Adapter
  (start* [this handler-fn])
  (send*  [this text]))

(defmacro defadapter
  [name & body]
  `(defrecord ~name [~'botname]
     ~'Adapter
     ~@body))

(defn set-adapter!
  [a]
  (reset! _adapter_ a))

(defn send!
  [text]
  (send* @_adapter_ text))

(defn text-to-bot
  [botname text]
  (let [prefix (str botname " ")]
    (if (and (string? text) (.startsWith text prefix))
      (apply str (drop (count prefix) text)))))

(defn start-adapter!
  [adapter handler-fn]
  (set-adapter! adapter)
  (start* @_adapter_ handler-fn))

