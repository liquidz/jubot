(ns jubot.require
  (:require
    [clojure.java.io              :as io]
    [clojure.tools.namespace.find :as find]))

(defn regexp-require
  [ns-regexp]
  (doseq [sym (->> (find/find-namespaces-in-dir (io/file "."))
                   (filter #(re-find ns-regexp (str %)))
                   (remove #(re-find #"-test$" (str %))))]
    (require sym)))
