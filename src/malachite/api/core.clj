(ns malachite.api.core
  (:require [malachite.api.user.model :as users]
            [malachite.api.track.model :as tracks]
            [malachite.api.playlist.model :as playlists]
            [malachite.api.user.handler :refer [add-user]]
            [malachite.api.track.handler :refer [add-track
                                                 find-user-tracks
                                                 add-track-to-playlist
                                                 find-playlist-tracks]]
            [malachite.api.playlist.handler :refer [add-playlist
                                                 find-user-playlists]]
            [malachite.api.soundcloud.wrapper :refer [likes]])
  
  (:use ring.middleware.json)
  (:use alex-and-georges.debug-repl)

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
   (= (:content-type req) "application/json")
   (= (lower-case (str (get-in req [:params "format"])))
      "json")))

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
   (POST "/users" [] add-user)
   (GET "/users/:user_id/tracks" [user-id] find-user-tracks)
   (POST "/users/:user_id/playlists" [user-id] add-playlist)
   (GET "/users/:user_id/playlists" [user-id] find-user-playlists)
   (GET "/playlists/:playlist_id" [playlist-id] find-playlist-tracks)
   (PUT "/tracks/:track_id" [track-id] add-track-to-playlist)
   (POST "/tracks" [] add-track)
   (GET "/likes" [] likes)))

(defroutes app-routes
  (context "/api" [] (ensure-json (api-routes)))
  (GET "/" [] greet)
  (ANY "/request" [] handle-dump)
  (not-found "Page not found."))

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :malachite.api/db db))))

(def app
  (-> app-routes
      wrap-db
      wrap-file-info
      wrap-json-response
      wrap-json-body
      wrap-params))

(defn init []
  (users/create-table db)
  (playlists/create-table db)
  (tracks/create-table db))

(defn -main [port]
   (init)
   (jetty/run-jetty app
                    {:port (Integer. port)}))

 (defn -dev-main [port]
   (init)
   (jetty/run-jetty (wrap-reload #'app)
                    {:port (Integer. port)}))
