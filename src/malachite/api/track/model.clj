(ns malachite.api.track.model
  (:use alex-and-georges.debug-repl)
  (:require [clojure.java.jdbc :as db]))

(defn create-relationship-tables [db]
  (db/execute!
    db
    ["CREATE TABLE IF NOT EXISTS user_tracks
      (user_id INTEGER REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
      track_id INTEGER REFERENCES tracks (track_id) ON UPDATE CASCADE NOT NULL,
      position INTEGER,
      CONSTRAINT user_track_pkey PRIMARY KEY (user_id, track_id));"]))

(defn create-table [db]
  (db/execute!
   db
   ["CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""])
  (db/execute!
   db
   ["CREATE TABLE IF NOT EXISTS tracks
    (track_id SERIAL PRIMARY KEY,
    soundcloud_id INTEGER NOT NULL UNIQUE,
    date_created TIMESTAMPTZ NOT NULL DEFAULT now());"])
  (create-relationship-tables db))

(defn create-track [db user-id soundcloud-id]
  (try
    (let [track (first 
                  (db/query
                  db
                  ["INSERT INTO tracks (soundcloud_id)
                   VALUES (?)
                   RETURNING *"
                  (Integer. soundcloud-id)]))]
      (db/query
        db
        ["INSERT INTO user_tracks (track_id, user_id)
          VALUES (?,?)
         RETURNING *"
        (:track_id track)
        (Integer. user-id)])
      track)
    (catch Exception e {:error (.getMessage e)})))

; TODO
; (defn update-track [db user-id soundcloud-id]
;   (try
;     (let [track (first (db/query
;                  db
;                  ["INSERT INTO tracks (soundcloud_id)
;                    VALUES (?)
;                    RETURNING soundcloud_id"
;                   (Integer. soundcloud-id)]))]
;       (db/query
;         db
;         ["INSERT INTO user_tracks (track_id, user_id)"])
;       track)
    
;     (catch Exception e {:error "Error creating track"})))

(defn find-tracks-by-user [db id]
  (db/query
    db
    ["SELECT * FROM users WHERE id = ?"
     id]))