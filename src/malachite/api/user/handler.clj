(ns malachite.api.user.handler
  (:require [malachite.api.user.model :refer [create-or-update-user
                                              save-likes
                                              find-user
                                              get-user-playqueue]]
            [malachite.api.playlist.model :refer [create-playlist]]
            [ring.util.response :refer [response status]]))

(defn auth-user [req]
  (let [db (:malachite.api/db req)
        username (get-in req [:body "username"])
        soundcloud-id (get-in req [:body "soundcloud_id"])
        access-token (get-in req [:body "access_token"])
        res (create-or-update-user db username soundcloud-id access-token)]
    (if-not (res :error)
      (do 
        (save-likes db (:user_id res) (:soundcloud_id res) (:access_token res))
        (create-playlist db (:user_id res) "Playqueue" false true)
        (response res))
      (->
        (response res)
        (status 400)))))

(defn get-user [req]
  (let [db (:malachite.api/db req)
        id (get-in req [:params :user_id])
        res (find-user db id)]
    (if-not (res :error)
      (response res)
      (->
        (response res)
        (status 404)))))

(defn get-playqueue [req]
  (let [db (:malachite.api/db req)
        user-id (get-in req [:params :user_id])
        res (get-user-playqueue db user-id)]
      (response res)))
