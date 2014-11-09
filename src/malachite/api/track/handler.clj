(ns malachite.api.track.handler
  (:use alex-and-georges.debug-repl)
  (:require [malachite.api.track.model :refer [create-track
                                               find-by-user
                                               update-track
                                               find-by-playlist]]
            [ring.util.response :refer [response status]]))

(defn add-track [req]
  (let [db (:malachite.api/db req)
        user-id (get-in req [:body "user_id"])
        soundcloud-id (get-in req [:body "soundcloud_id"])
        res (create-track db user-id soundcloud-id)]
    (if-not (:error res)
      (response res)
      (->
        (response res)
        (status 400)))))

(defn add-track-to-playlist [req]
  (let [db (:malachite.api/db req)
        playlist-id (get-in req [:body "playlist_id"])
        track-id (get-in req [:params :track_id])
        position (get-in req [:body "position"])
        res (update-track db playlist-id track-id position)]
    (if-not (:error res)
      (response res)
      (->
        (response res)
        (status 400)))))

(defn find-user-tracks [req]
  (let [db (:malachite.api/db req)
        user-id (get-in req [:params :user_id])
        res (find-by-user db (Integer. user-id))]
    (if-not (:error res)
      (response res)
      (->
        (response res)
        (status 400)))))

(defn find-playlist-tracks [req]
  (let [db (:malachite.api/db req)
        playlist-id (get-in req [:params :playlist_id])
        res (find-by-playlist db (Integer. playlist-id))]
    (if-not (:error res)
      (response res)
      (->
        (response res)
        (status 400)))))