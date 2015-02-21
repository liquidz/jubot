(defproject jubot "0.0.1-SNAPSHOT-9"
  :description "Chatbot framework in Clojure"
  :url "https://github.com/liquidz/jubot"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure         "1.6.0"]
                 [com.stuartsierra/component  "0.2.2"]
                 [org.clojure/tools.namespace "0.2.9"]
                 [org.clojure/tools.cli       "0.3.1"]
                 [compojure                   "1.3.1"]
                 [ring/ring-jetty-adapter     "1.3.2"]
                 [ring/ring-defaults          "0.1.3"]
                 [org.clojure/data.json       "0.2.5"]
                 [com.taoensso/carmine        "2.9.0"]
                 [com.taoensso/timbre         "3.3.1"]
                 [clj-http-lite               "0.2.1"]
                 [im.chit/cronj               "1.4.3"]]

  :profiles {:dev {:dependencies
                   [[org.clojars.runa/conjure "2.2.0"]
                    [ring/ring-mock           "0.2.0"]]
                   :source-paths ["dev"]}}

  :codox {:exclude [user dev]
          :src-dir-uri "http://github.com/liquidz/jubot/blob/master/"
          :src-linenum-anchor-prefix "L"}

  :aliases {"dev" ["run" "-m" "dev/-main"]})
