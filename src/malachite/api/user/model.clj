(ns malachite.api.user.model
  (:use alex-and-georges.debug-repl)
  (:require [clojure.java.jdbc :as db]))

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

(defn find-user [db id]
  (db/query
    db
    ["SELECT * FROM users WHERE id = ?"
     id]))