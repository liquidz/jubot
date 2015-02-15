(ns jubot.di
  "テスト時にスタブ化できるように関数定義する")

(def ^{:doc "clojure.core/println"}
  println* println)

(def ^{:doc "System/getenv"}
  getenv*  #(System/getenv %))
