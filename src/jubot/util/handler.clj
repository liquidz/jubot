(ns jubot.util.handler)

(defn regexp-handler
  [& reg-fn-list]
  {:pre [(zero? (mod (count reg-fn-list) 2))]}

  (fn [text]
    (reduce
      (fn [_ [r f]]
        (if (instance? java.util.regex.Pattern r)
          (some->> (re-seq r text)
                   f
                   reduced)
          (reduced (f text))))
      nil
      (partition 2 reg-fn-list))))
