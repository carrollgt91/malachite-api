(ns malachite.api.soundcloud.wrapper
  (:require [clj-http.client :as http]
            [cheshire.core :refer :all]))

(defn- streamable? [track]
  (get track "streamable"))

(defn likes [user-id access-token]
  (let [resp (http/get 
              (str "https://api.soundcloud.com/users/" 
              user-id 
              "/favorites?oauth_token="
              access-token
              "&format=json"))
        parsed-resp (parse-string (:body resp))
        filtered-resp (filter streamable? parsed-resp)]
    filtered-resp))