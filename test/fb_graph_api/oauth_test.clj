(ns fb-graph-api.oauth-test
  (:require [fb-graph-api.oauth :refer :all]
            [midje.sweet :refer :all]
            [org.httpkit.client :as http]))

(facts "about login-url"
  (fact "calculating login url"
    (login-url "1234567890" "https://localhost/fblogin") =>
    (str "https://www.facebook.com/dialog/oauth?"
         "client_id=1234567890&redirect_uri=https%3A%2F%2Flocalhost%2Ffblogin"))
  (fact "passing required permissions"
    (login-url "1234567890" "https://localhost/fblogin" ["a" "b" "c"]) =>
    (str "https://www.facebook.com/dialog/oauth?"
         "client_id=1234567890&redirect_uri=https%3A%2F%2Flocalhost%2Ffblogin&scope=a%2Cb%2Cc")))

(facts "#access-token"
  (fact "Exchanging correct code to user token"
    (access-token ..app_id.. ..app_secret.. ..redirect_uri.. ..code..) =>
    {:access-token "TOKEN" }
    (provided
      (http/get "https://graph.facebook.com/v2.3/oauth/access_token"
                {:query-params {:client_id ..app_id..
                                :client_secret ..app_secret..
                                :redirect_uri ..redirect_uri..
                                :code ..code..}}) =>
        (delay
         {:status 200
          :body "{\"access_token\":\"TOKEN\",\"token_type\":\"bearer\",\"expires_in\":5181690}"})))

  (fact "Exchanging incorrect code to user token throws exception"
    (access-token ..app_id.. ..app_secret.. ..redirect_uri.. ..bad_code..) =>
    (throws Exception "Failed to retrieve access token because of error: ERROR")
    (provided
      (http/get "https://graph.facebook.com/v2.3/oauth/access_token"
                {:query-params {:client_id ..app_id..
                                :client_secret ..app_secret..
                                :redirect_uri ..redirect_uri..
                                :code ..bad_code..}}) =>
        (delay
         {:status 500
          :body (str "{\"error\": {\"message\": \"ERROR\","
                     "\"type\": \"OAuthException\",\"code\": 100}}")})))

  (fact "Error while exchanging data throws exception"
    (access-token ..app_id.. ..app_secret.. ..redirect_uri.. ..code..) =>
    (throws Exception "Request failed")
    (provided
      (http/get "https://graph.facebook.com/v2.3/oauth/access_token"
                {:query-params {:client_id ..app_id..
                                :client_secret ..app_secret..
                                :redirect_uri ..redirect_uri..
                                :code ..code..}}) =>
        (delay
         {:error (Exception. "Some Exception")}))))

(facts "#request-app-only-token"
  (fact "Requesting app token using correct parameters"
    (request-app-only-token ..app_id.. ..app_secret..) =>
    {:access-token "TOKEN" }
    (provided
      (http/get "https://graph.facebook.com/v2.3/oauth/access_token"
                {:query-params {:client_id ..app_id..
                                :client_secret ..app_secret..
                                :grant_type "client_credentials"}}) =>
        (delay
         {:status 200
          :body "{\"access_token\":\"TOKEN\",\"token_type\":\"bearer\"}"})))

  (fact "Exchanging incorrect code to user token throws exception"
    (request-app-only-token ..app_id.. ..app_secret..) =>
    (throws Exception "Failed to retrieve access token because of error: ERROR")
    (provided
      (http/get "https://graph.facebook.com/v2.3/oauth/access_token"
                {:query-params {:client_id ..app_id..
                                :client_secret ..app_secret..
                                :grant_type "client_credentials"}}) =>
        (delay
         {:status 500
          :body (str "{\"error\": {\"message\": \"ERROR\","
                     "\"type\": \"OAuthException\",\"code\": 100}}")})))

  (fact "Error while exchanging data throws exception"
    (request-app-only-token ..app_id.. ..app_secret..) =>
    (throws Exception "Request failed")
    (provided
      (http/get "https://graph.facebook.com/v2.3/oauth/access_token"
                {:query-params {:client_id ..app_id..
                                :client_secret ..app_secret..
                                :grant_type "client_credentials"}}) =>
        (delay
         {:error (Exception. "Some Exception")}))))
