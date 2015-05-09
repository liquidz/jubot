(defproject jubot "0.1.0"
  :description "Chatbot framework in Clojure"
  :url "https://github.com/liquidz/jubot"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure         "1.6.0"]
                 [com.stuartsierra/component  "0.2.3"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [org.clojure/tools.cli       "0.3.1"]
                 [compojure                   "1.3.4"]
                 [ring/ring-jetty-adapter     "1.3.2"]
                 [ring/ring-defaults          "0.1.5"]
                 [org.clojure/data.json       "0.2.6"]
                 [com.taoensso/carmine        "2.10.0"]
                 [com.taoensso/timbre         "3.4.0"]
                 [clj-http-lite               "0.2.1"]
                 [im.chit/cronj               "1.4.3"]]

  :profiles {:dev {:dependencies
                   [[org.clojars.runa/conjure "2.2.0"]
                    [ring/ring-mock           "0.2.0"]]
                   :source-paths ["dev"]}}

  :codox {:exclude [user dev]
          :src-dir-uri "http://github.com/liquidz/jubot/blob/master/"
          :src-linenum-anchor-prefix "L"
          :output-dir "doc/api"}

  :aliases {"dev" ["run" "-m" "dev/-main"]})
