(defproject malachite "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.13"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [compojure "1.2.1"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [org.postgresql/postgresql "9.3-1102-jdbc4"]
                 [camel-snake-kebab "0.2.5"]
                 [cheshire "5.3.1"]
                 [clj-http "1.0.1"]
                 [rest-cljer "0.1.15"]
                 [midje "1.6.3"]
                 [environ "1.0.0"]
                 [ring-mock "0.1.5"]]

  :main malachite.api.core

  :ring {:handler malachite.api.core/app
         :init malachite.api.core/init}

  :min-lein-version "2.0.0"

  :uberjar-name "malachite.jar"

  :profiles {:dev
             {:main malachite.api.core/-dev-main}})
