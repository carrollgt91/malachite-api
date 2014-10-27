(defproject webdev "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.1"]
                 [compojure "1.2.1"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [org.postgresql/postgresql "9.3-1102-jdbc4"]
                 [hiccup "1.0.5"]]
  :main webdev.core

  :min-lein-version "2.0.0"

  :uberjar-name "webdev.jar"

  :profiles {:dev
             {:main webdev.core/-dev-main}})
