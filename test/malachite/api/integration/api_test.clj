(ns malachite.api.integration.api-test
  (:use midje.sweet
        peridot.core)
  (:require [malachite.api.core]))

(fact "the root url should return hello world"
  (get-in (-> (session malachite.api.core/app)
      (request "/")) [:response :body]) => "hello world")
