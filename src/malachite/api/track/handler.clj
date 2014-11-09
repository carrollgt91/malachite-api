(ns malachite.api.track.handler
  (:use alex-and-georges.debug-repl)
  (:require [malachite.api.track.model :refer [create-track
                                               find-by-user]]
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

(defn find-user-tracks [req]
  (let [db (:malachite.api/db req)
        user-id (get-in req [:params :user_id])
        res (find-by-user db (Integer. user-id))]
    (if-not (:error res)
      (response res)
      (->
        (response res)
        (status 400)))))
