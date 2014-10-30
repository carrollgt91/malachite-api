(ns malachite.api.core
  (:require [malachite.api.item.model :as items]
            [malachite.api.item.handler :refer [handle-index-items
                                                handle-create-item]])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]))

(def db (or
         (System/getenv "DATABASE_URL")
         "jdbc:postgresql://localhost/malachite-api-dev"))

(defn greet [req]
  {:status 200
   :body "hello world"
   :headers {}})

(defroutes routes
  (GET "/" [] greet)
  (ANY "/request" [] handle-dump)
  (GET "/items" [] handle-index-items)
  (POST "/items" [] handle-create-item)
  (not-found "Page not found."))

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :malachite.api/db db))))

(defn wrap-server [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server:"] "Malachite API")))

(def app
  (wrap-db
   (wrap-file-info
     (wrap-params
      (wrap-server
        routes))
     "static")))

(defn init []
  (items/create-table db))

(defn -main [port]
   (init)
   (jetty/run-jetty app
                    {:port (Integer. port)}))

 (defn -dev-main [port]
   (init)
   (jetty/run-jetty (wrap-reload #'app)
                    {:port (Integer. port)}))
