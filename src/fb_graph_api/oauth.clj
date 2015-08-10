(ns fb-graph-api.oauth
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [fb-graph-api.util :as u]
            [org.httpkit.client :as http]))

(def ^{:private true} authorize-url "https://www.facebook.com/dialog/oauth")

(defn generate-query-string
  "Generates query string"
  [params]
  (let [param (fn [k v] (str (http/url-encode (name k)) "=" (http/url-encode v)))
        join (fn [strs] (str/join "&" strs))]
    (join (for [[k v] params] (param k v)))))

(defn login-url
  "Construct login URL for authenticate user and generate user's access token"
  ([app-id redirect-uri] (login-url app-id redirect-uri nil))

  ([app-id redirect-uri permissions]

   (let [common-params {:client_id app-id
                        :redirect_uri redirect-uri}
         params (if (seq permissions)
                  (assoc common-params :scope (str/join "," permissions))
                  common-params)]
     (str authorize-url
          "?" (generate-query-string params)))))

(defn access-token
  "Exchange a code for an access token"
  [app-id app-secret redirect-uri code]

  (let [params {:client_id app-id
                :redirect_uri redirect-uri
                :client_secret app-secret
                :code code}
        url (u/graph-url ["oauth" "access_token"])]
    (u/load-graph-data url params)))

(defn exchange-access-token
  "Exchange access token (short-term for long-term)"
  [app-id app-secret old-token]

  (let [params {:client_id app-id
                :client_secret app-secret
                :grant_type "fb_exchange_token"
                :fb_exchange_token old-token}
        url (u/graph-url ["oauth" "access_token"])]
    (u/load-graph-data url params)))

(defn debug_token
  "Returns information about token"
  [app-token token]
  (let [params {:access_token app-token
                :input_token token}
        url (u/graph-url ["debug_token"])]
    (u/load-graph-data url params)))

(defn request-app-only-token
  "Requests token for application-level access to Graph API"
  [app-id app-secret]

  (let [params {:client_id app-id
                :client_secret app-secret
                :grant_type "client_credentials"}
        url (u/graph-url ["access_token"])]
    (u/load-graph-data url params)))
