; (ns malachite.api.integration.api-test
;   (:use midje.sweet
;         malachite.api.core
;         ring.mock.request
;         cheshire.core))

; (fact "the root url should return hello world"
;   ((app (request :get "/")) :body) => "hello world")

; (facts "lets should work inside a fact"
;   (let [response (app (request :get "/api"))]
;     (get-in response [:headers "Content-Type"]) => "application/json; charset=utf-8"
;     (response :status)
;             => 406
;     ((parse-string (response :body)) "message") => "We only talk in JSON, dawg"))