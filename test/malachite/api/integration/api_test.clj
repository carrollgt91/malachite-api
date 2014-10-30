(ns malachite.api.integration.api-test
  (:use midje.sweet)
  (:require [malachite.api.core :as core]))

(fact "this should run somehow and will test 1 + 1"
  (+ 1 1) => 2
  (+ 2 2) => 4)
