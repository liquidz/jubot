(ns jubot.redef
  "Redefining function fot stubbing.")

(def ^{:doc "clojure.core/println"}
  println* println)

(def ^{:doc "System/getenv"}
  getenv*  #(System/getenv %))
