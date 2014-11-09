(ns malachite.api.playlist.handler
  (:use alex-and-georges.debug-repl)
  (:require [malachite.api.playlist.model :refer [create-playlist
                                                  find-by-user]]
            [ring.util.response :refer [response status]]))

(defn add-playlist [req]
  (let [db (:malachite.api/db req)
        user-id (get-in req [:params :user_id])
        title (get-in req [:body "title"])
        is-likes (get-in req [:body "isLikes"])
        res (create-playlist db user-id title is-likes)]
    (if-not (:error res)
      (response res)
      (->
        (response res)
        (status 400)))))

(defn find-user-playlists [req]
  (let [db (:malachite.api/db req)
        user-id (get-in req [:params :user_id])
        res (find-by-user db (Integer. user-id))]
    (if-not (:error res)
      (response res)
      (->
        (response res)
        (status 400)))))
