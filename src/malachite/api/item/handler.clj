(ns malachite.api.item.handler
  (:require [malachite.api.item.model :refer [create-item
                                              read-items
                                              update-item
                                              delete-item]]))



(defn handle-index-items [req]
  (let [db (:malachite.api/db req)
        items (read-items db)]
    {:status 200
     :headers {}
     :body (str "<html><head></head><body>"
                (mapv :name items)
                "<form method=\"POST\" action=\"/api/items\">"
                  "<input type=\"text\" name=\"name\">"
                  "<input type=\"text\" name=\"description\">"
                  "<input type=\"hidden\" name=\"format\" value=\"json\">"
                  "<input type=\"submit\">"
                "</form></body></html>")}))


(defn handle-create-item [req]
  (let [db (:malachite.api/db req)
        name (get-in req [:params "name"])
        description (get-in req [:params "description"])
        item-id (create-item db name description)]
    {:status 302
     :headers {"Location" "items?format=json"}
     :body ""}))
