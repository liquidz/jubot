(ns jubot.require
  "Jubot utilities for requiring namespaces."
  (:require
    [clojure.java.io              :as io]
    [clojure.tools.namespace.find :as find]))

(defn regexp-require
  "Require namespaces which matches specified regular expression.

  Params
    ns-regexp - A regular expression which specifies namespaces.
  "
  [ns-regexp]
  (doseq [sym (->> (find/find-namespaces-in-dir (io/file "."))
                   (filter #(re-find ns-regexp (str %)))
                   (remove #(re-find #"-test$" (str %))))]
    (require sym)))
