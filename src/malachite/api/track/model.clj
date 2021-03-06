(ns malachite.api.track.model
  (:require [clojure.java.jdbc :as db]))

(defn create-user-track-table [db]
  (db/execute!
    db
    ["CREATE TABLE IF NOT EXISTS user_tracks
      (user_id INTEGER REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
      track_id INTEGER REFERENCES tracks (track_id) ON UPDATE CASCADE NOT NULL,
      position INTEGER,
      CONSTRAINT user_track_pkey PRIMARY KEY (user_id, track_id));"]))

(defn create-playlist-track-table [db]
  (db/execute!
    db
    ["CREATE TABLE IF NOT EXISTS playlist_tracks
      (playlist_id INTEGER REFERENCES playlists (playlist_id) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
      track_id INTEGER REFERENCES tracks (track_id) ON UPDATE CASCADE NOT NULL,
      position INTEGER,
      CONSTRAINT playlist_track_pkey PRIMARY KEY (playlist_id, track_id));"]))

(defn create-relationship-tables [db]
  (create-user-track-table db)
  (create-playlist-track-table db))

(defn create-table [db]
  (db/execute!
   db
   ["CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""])
  (db/execute!
   db
   ["CREATE TABLE IF NOT EXISTS tracks
    (track_id SERIAL PRIMARY KEY,
    soundcloud_id INTEGER NOT NULL UNIQUE,
    title VARCHAR(256),
    username VARCHAR(64),
    artwork_url VARCHAR(256),
    date_created TIMESTAMPTZ NOT NULL DEFAULT now());"])
  (create-relationship-tables db))

(defn create-track [db user-id sc-track]
  (try
    (let [track (first 
                  (db/query
                  db
                  ["INSERT INTO tracks (soundcloud_id, title, username, artwork_url)
                   VALUES (?,?,?,?)
                   RETURNING *"
                  (get sc-track "id")
                  (get sc-track "title")
                  (get-in sc-track ["user" "username"])
                  (get sc-track "artwork_url")]))]
      (db/query
        db
        ["INSERT INTO user_tracks (track_id, user_id)
          VALUES (?,?)
         RETURNING *"
        (:track_id track)
        (Integer. user-id)])
      track)
    (catch Exception e {:error (.getMessage e)})))

(defn update-track [db playlist-id track-id position]
  (try
    (db/query
      db
      ["INSERT INTO playlist_tracks (track_id, playlist_id, position)
       VALUES (?,?,?)
       RETURNING *"
       (Integer. track-id)
       (Integer. playlist-id)
       (Integer. position)])
    
    (catch Exception e {:error (.getMessage e)})))

(defn find-by-user [db user-id]
  (let [tracks (db/query
    db
    ["SELECT * FROM tracks, user_tracks
      WHERE tracks.track_id = user_tracks.track_id
        AND user_tracks.user_id = ?;"
     user-id])]
    tracks))

(defn find-by-playlist [db playlist-id]
  (let [tracks (db/query
    db
    ["SELECT * FROM tracks, playlist_tracks
      WHERE tracks.track_id = playlist_tracks.track_id
        AND playlist_tracks.playlist_id = ?;"
     playlist-id])]
    tracks))