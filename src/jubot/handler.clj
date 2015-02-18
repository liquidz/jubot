(ns jubot.handler
  "Jubot handler utilities."
  (:refer-clojure :exclude [comp]))

(def ^{:const true
       :doc "The handler name regular expression for collecting handler functions automatically."}
  HANDLER_REGEXP #"^.*-handler$")

(defn regexp
  "Generate a handler function from pair of regular expression and function.

  Params
    reg-fn-list - Pair of regular expression and function.
                  If the regular expression is matched, the paired function is called.
                  In addition to original handler input,
                  `re-find` result will be passed to the paired function.
  Return
    A handler function.
  "
  [& reg-fn-list]
  {:pre [(zero? (mod (count reg-fn-list) 2))]}

  (fn [{:keys [text] :as option}]
    (when text
      (reduce
        (fn [_ [r f]]
          (if (instance? java.util.regex.Pattern r)
            (some->> (re-find r text)
                     (assoc option :match)
                     f
                     reduced)
            (reduced (f option))))
        nil
        (partition 2 reg-fn-list)))))

(defn comp
  "Compose handler functions.

  Params
    fs - Sequence of handler functions.
  Return
    Composition of handler functions.
  "
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
  "Return sequence of public handler functions which matched HANDLER_REGEXP in specified namespaces.

  Params
    ns-regexp - A regular expression which specifies namespaces for searching handler functions.
  Return
    Sequence of handler functions.
  "
  [ns-regexp]
  (->> (all-ns)
       (filter #(re-find ns-regexp (str (ns-name %))))
       (mapcat #(vals (ns-publics %)))
       (filter #(re-matches HANDLER_REGEXP (-> % meta :name str)))))

(defn collect
  "Return composition of public handler functions in specified namespaces.

  Params
    ns-regexp - A regular expression which specifies namespaces for searching handler functions.
  Return
    A handler function.
  "
  [ns-regexp]
  (if-let [handlers (seq (public-handlers ns-regexp))]
    (apply comp handlers)
    (constantly nil)))
