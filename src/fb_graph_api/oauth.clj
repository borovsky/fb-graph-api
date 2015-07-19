(ns fb-graph-api.oauth
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [org.httpkit.client :as http]))

(def ^{:private true} authorize-url "https://www.facebook.com/dialog/oauth")
(def ^{:private true} access-url "https://graph.facebook.com/v2.3/oauth/access_token")
(def ^{:private true} debug-token-url "https://graph.facebook.com/v2.3/debug_token")

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
        response @(http/get access-url {:query-params params})]

    (cond
      (= 200 (:status response))
        {:access-token  (get-in (json/read-str (:body response)) ["access_token"])}
      (:error response)
        (throw (Exception. (str "Request failed") (:error response)))
      :else (throw (Exception.
                    (str "Failed to retrieve access token because of error: "
                         (get-in (json/read-str (:body response)) ["error" "message"])))))))

(defn exchange-access-token
  "Exchange access token (short-term for long-term)"
  [app-id app-secret old-token]

  (let [params {:client_id app-id
                :client_secret app-secret
                :grant_type "fb_exchange_token"
                :fb_exchange_token old-token}
        response @(http/get access-url {:query-params params})]

    (cond
      (= 200 (:status response))
        {:access-token  (get-in (json/read-str (:body response)) ["access_token"])}
      (:error response)
        (throw (Exception. (str "Request failed") (:error response)))
      :else (throw (Exception.
                    (str "Failed to retrieve access token because of error: "
                         (get-in (json/read-str (:body response)) ["error" "message"])))))))

(defn debug_token
  "Returns information about token"
  [app-token token]
  (let [params {:access_token app-token
                :input_token token}
        response @(http/get debug-token-url {:query-params params})]

    (cond
      (= 200 (:status response))
      (get-in (json/read-str (:body response)) ["data"])
      (:error response)
        (throw (Exception. (str "Request failed") (:error response)))
      :else (throw (Exception.
                    (str "Failed to retrieve access token because of error: "
                         (get-in (json/read-str (:body response)) ["error" "message"])))))))

(defn request-app-only-token
  "Requests token for application-level access to Graph API"
  [app-id app-secret]

  (let [params {:client_id app-id
                :client_secret app-secret
                :grant_type "client_credentials"}
        response @(http/get access-url {:query-params params})]

    (cond
      (= 200 (:status response))
        {:access-token  (get-in (json/read-str (:body response)) ["access_token"])}
      (:error response)
        (throw (Exception. (str "Request failed") (:error response)))
      :else (throw (Exception.
                    (str "Failed to retrieve access token because of error: "
                         (get-in (json/read-str (:body response)) ["error" "message"])))))))
