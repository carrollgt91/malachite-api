(ns malachite.api.core
  (:require [malachite.api.item.model :as items]
            [malachite.api.item.handler :refer [handle-index-items
                                                handle-create-item]])
  (:use ring.middleware.json)

  (:require [clojure.string :refer [lower-case]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [routes defroutes context ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.util.response :refer [response status]]))

(def db (or
         (System/getenv "DATABASE_URL")
         "jdbc:postgresql://localhost/malachite-api-dev"))

(defn- is-json-req [req]
  (or
   (= (:content-type req) "application/json"))
   (= (lower-case (str (get-in req [:params "format"])))
      "json"))

(defn ensure-json [hdlr]
  (fn [req]
    (if (is-json-req req)
      (hdlr req)
      (-> (response {:message "We only talk in JSON, dawg"})
          (status 406)))))

(defn greet [req]
  {:status 200
   :body "hello world"
   :headers {}})

(defn api-routes []
  (routes
   (GET "/" [] (response {:root true}))
   (GET "/items" [] handle-index-items)
   (POST "/items" [] handle-create-item)))

(defroutes app-routes
  (context "/api" [] (ensure-json (api-routes)))
  (GET "/" [] greet)
  (ANY "/request" [] handle-dump)
  (not-found "Page not found."))

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :malachite.api/db db))))

(def app
  (wrap-db
   (wrap-file-info
     (wrap-params
      (wrap-json-response
       app-routes)))))

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
