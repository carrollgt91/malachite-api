(ns malachite.api.playlist.handler
  (:require [malachite.api.playlist.model :refer [create-playlist
                                                  find-by-user]]
            [ring.util.response :refer [response status]]))

(defn add-playlist [req]
  (let [db (:malachite.api/db req)
        user-id (get-in req [:params :user_id])
        title (get-in req [:body "title"])
        is-likes (get-in req [:body "is_likes"])
        is-playqueue (get-in req [:body "is_playqueue"])
        res (create-playlist db user-id title is-likes is-playqueue)]
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
