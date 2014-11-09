(ns malachite.api.playlist.model
  (:use alex-and-georges.debug-repl)
  (:require [clojure.java.jdbc :as db]))

(defn create-table [db]
  (db/execute!
   db
   ["CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""])
  (db/execute!
   db
   ["CREATE TABLE IF NOT EXISTS playlists
    (playlist_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users (user_id) ON UPDATE CASCADE NOT NULL,
    title VARCHAR(64) NOT NULL,
    is_likes BOOLEAN NOT NULL DEFAULT false,
    is_playqueue BOOLEAN NOT NULL DEFAULT false,
    date_created TIMESTAMPTZ NOT NULL DEFAULT now());"]))

(defn create-playlist [db user-id title is-likes is-playqueue]
  (try
    (first 
      (db/query
       db
       ["INSERT INTO playlists (user_id, title, is_likes, is_playqueue)
         VALUES (?, ?, ?, ?)
         RETURNING *"
        (Integer. user-id)
        title,
        is-likes
        is-playqueue]))
    (catch Exception e {:error (.getMessage e)})))

(defn find-by-user [db user-id]
  (let [playlists (db/query
    db
    ["SELECT * FROM playlists
      WHERE playlists.user_id = ?;"
     user-id])]
    playlists))