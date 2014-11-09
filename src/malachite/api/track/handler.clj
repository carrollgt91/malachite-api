(ns malachite.api.track.handler
  (:use alex-and-georges.debug-repl)
  (:require [malachite.api.track.model :refer [create-track
                                               find-tracks-by-user]]
            [ring.util.response :refer [response status]]))

(defn add-track [req]
  (let [db (:malachite.api/db req)
        user-id (get-in req [:body "user_id"])
        soundcloud-id (get-in req [:body "soundcloud_id"])
        res (create-track db user-id soundcloud-id)]
    (if-not (res :error)
      (response res)
      (->
        (response res)
        (status 400)))))
