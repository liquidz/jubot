(ns jubot.handler
  "Jubot handler utilities."
  (:require [clojure.string :as str])
  (:refer-clojure :exclude [comp]))

(def ^{:const true
       :doc "The handler name regular expression for collecting handler functions automatically."}
  HANDLER_REGEXP #"^.*-handler$")

(defn regexp
  "Choose and call handler function by specified regular expression.

  Params
    option      - An argument that is passed to original handler function.
    reg-fn-list - Pair of regular expression and function.
                  If the regular expression is matched, the paired function is called.
                  In addition to original handler input,
                  `re-find` result will be passed to the paired function with `match` key.
  Return
    Result of a chosen handler function.
  "
  [{:keys [text] :as option} & reg-fn-list]
  {:pre [(zero? (mod (count reg-fn-list) 2))]}
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
      (partition 2 reg-fn-list))))

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
       (remove #(re-find #"-test$" (str (ns-name %))))
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

(defn help-handler-fn
  "Returns handler function to show handler helps.

  Params
    :ns-regexp - a regular-expression which specifies bot's namespace.
  Return
    A handler function.
  "
  [ns-regexp]
  (fn [{text :text, forme? :message-for-me?}]
    (when (and forme? (= "help" text))
      (->> (public-handlers ns-regexp)
           (map #(-> % meta :doc))
           (remove #(or (nil? %) (= % "")))
           (mapcat str/split-lines)
           (map str/trim)
           (str/join "\n")
           (str "Help documents:\n---\n")))))
