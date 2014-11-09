(ns malachite.api.user.handler
  (:use alex-and-georges.debug-repl)
  (:require [malachite.api.user.model :refer [create-user
                                              save-likes]]
            [ring.util.response :refer [response status]]))

(defn add-user [req]
  (let [db (:malachite.api/db req)
        username (get-in req [:body "username"])
        soundcloud-id (get-in req [:body "soundcloud_id"])
        res (create-user db username soundcloud-id)]
    (if-not (res :error)
      (do 
        (save-likes db (:user_id res) (:soundcloud_id res))
        (response res))
      (->
        (response res)
        (status 400)))))
