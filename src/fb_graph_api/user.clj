(ns fb-graph-api.user
  (:require [fb-graph-api.util :refer [def-graph-get-request]]))

(def-graph-get-request "profile" [:user-id] "Fetches user profile")

(def-graph-get-request "feed" [:user-id "feed"] "Fetches user feed")
