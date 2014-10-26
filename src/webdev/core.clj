(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]))

(defn greet [req]
  {:status 200
   :body "hello world"
   :headers {}})

(defn goodbye [req]
  {:status 200
   :body "goodbye, cruel world"
   :headers {}})

(defn request [req]
  {:status 200
   :body (str req)
   :headers {}})

(defroutes app
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (GET "/request" [] handle-dump)
  (not-found "Page not found."))

(defn -main [port]
   (jetty/run-jetty app
                    {:port (Integer. port)}))

 (defn -dev-main [port]
   (jetty/run-jetty (wrap-reload #'app)
                    {:port (Integer. port)}))
