(ns jubot.util.handler)

(defn regexp-handler
  [& reg-fn-list]
  {:pre [(zero? (mod (count reg-fn-list) 2))]}

  (fn [{:keys [text] :as option}]
    (reduce
      (fn [_ [r f]]
        (if (instance? java.util.regex.Pattern r)
          (some->> (re-seq r text)
                   (assoc option :match)
                   f
                   reduced)
          (reduced (f option))))
      nil
      (partition 2 reg-fn-list))))
