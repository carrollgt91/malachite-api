(ns malachite.api.soundcloud.wrapper
  (:require [clj-http.client :as http]
            [cheshire.core :refer :all])
  (:use alex-and-georges.debug-repl))

(def ^:private client-id "251c9152fb3757d609504877ed494ae0")

(defn likes [user-id]
  (let [resp (http/get 
              (str "https://api.soundcloud.com/users/" 
              "9027201" 
              "/favorites?client_id="
              client-id
              "&format=json"))
        parsed-resp (parse-string (:body resp))]
    parsed-resp))