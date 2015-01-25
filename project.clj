(defproject jubot "0.0.1"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [compojure "1.3.1"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [ring/ring-devel "1.3.2"]
                 [org.clojure/data.json "0.2.5"]
                 [com.taoensso/carmine "2.9.0"]]
  :uberjar-name "jubot-standalone.jar"

  :profiles {:dev {:dependencies
                   [[midje "1.6.3" :exclusions [org.clojure/clojure]]
                    [ring/ring-mock "0.2.0"]
                    [org.clojars.runa/conjure "2.2.0"] ]}}
  :plugins [[lein-midje "3.1.3"]]

  ;:aliases {"dev-run" ["run" "-m" "jubot.server/-dev-main"]}
  :min-lein-version "2.0.0"
  ;:main jubot.server
  :main jubot.core
  )
