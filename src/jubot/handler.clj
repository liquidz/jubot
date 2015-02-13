(ns jubot.handler
  (:refer-clojure :exclude [comp]))

(def ^:const HANDLER_REGEXP #".+?-handler$")

(defn regexp
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

(defn comp
  ([] identity)
  ([& fs]
   {:pre [(every? #(or (fn? %) (var? %)) fs)]}
   (let [fs (reverse fs)]
     (fn [arg]
       (loop [ret ((first fs) arg), fs (next fs)]
         (if (and fs (nil? ret))
           (recur ((first fs) arg) (next fs))
           ret))))))

(defn public-handlers
  [ns-regexp]
  (->> (all-ns)
       (filter #(re-find ns-regexp (str (ns-name %))))
       (mapcat #(vals (ns-publics %)))
       (filter #(re-matches HANDLER_REGEXP (-> % meta :name str)))))

(defn collect
  [ns-regexp]
  (apply comp (public-handlers ns-regexp)))
