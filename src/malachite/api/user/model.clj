(ns malachite.api.user.model
  (:use alex-and-georges.debug-repl)
  (:require [clojure.java.jdbc :as db]
            [malachite.api.soundcloud.wrapper :as sc]
            [malachite.api.track.model :as track-model]))

(defn create-table [db]
  (db/execute!
   db
   ["CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""])
  (db/execute!
   db
   ["CREATE TABLE IF NOT EXISTS users
    (user_id SERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    soundcloud_id INTEGER NOT NULL UNIQUE,
    date_created TIMESTAMPTZ NOT NULL DEFAULT now())"]))

(defn create-user [db username soundcloud-id]
  (try
    (first (db/query
                 db
                 ["INSERT INTO users (username, soundcloud_id)
                   VALUES (?, ?)
                   RETURNING *"
                  username
                  (Integer. soundcloud-id)]))
    (catch Exception e {:error "User already exists"})))

(defn save-likes [db user-id user-soundcloud-id]  
  (let [likes (sc/likes user-soundcloud-id)]
    (loop [tracks likes]
      (when (seq tracks)
        (track-model/create-track db user-id (first tracks))
        (recur (rest tracks))))))

(defn get-user-playqueue [db user-id]
  (let [playqueue-id
        (first (db/query
          db
          ["SELECT playlist_id FROM playlists
            WHERE playlists.user_id = ?
              AND playlists.is_playqueue = true;"
              (Integer. user-id)]))
        tracks
          (track-model/find-by-playlist db (get playqueue-id :playlist_id))]
    tracks))

(defn find-user [db id]
  (db/query
    db
    ["SELECT * FROM users WHERE id = ?"
     id]))