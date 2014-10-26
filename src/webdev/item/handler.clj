(ns webdev.item.handler
  (:require [webdev.item.model :refer [create-item
                                       read-items
                                       update-item
                                       delete-item]]))



(defn handle-index-items [req]
  (let [db (:webdev/db req)
        items (read-items db)]
    {:status 200
     :headers {}
     :body (str "<html><head></head><body>"
                (mapv :name items)
                "<form method=\"POST\" action=\"/items\">"
                  "<input type=\"text\" name=\"name\">"
                  "<input type=\"text\" name=\"description\">"
                  "<input type=\"submit\">"
                "</form></body></html>")}))


(defn handle-create-item [req]
  (let [db (:webdev/db req)
        name (get-in req [:params "name"])
        description (get-in req [:params "description"])
        item-id (create-item db name description)]
    {:status 302
     :headers {"Location" "/items"}
     :body ""}))
