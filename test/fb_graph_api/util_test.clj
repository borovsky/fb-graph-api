(ns fb-graph-api.util-test
  (:require [fb-graph-api.util :refer :all]
            [midje.sweet :refer :all]
            [org.httpkit.client :as http]))

(facts "about def-graph-get-request"
  (fact "expand"
    (def-graph-get-request "feed" [:user-id "feed"] "Fetches user info") =expands-to=>
    (clojure.core/defn feed
      "Fetches user info"
      ([token user-id] (feed token user-id {}))
      ([token user-id params]
       (clojure.core/let [url (fb-graph-api.util/graph-url [user-id "feed"])]
         (fb-graph-api.util/load-graph-data url (clojure.core/merge params {:access_token token})))))))
