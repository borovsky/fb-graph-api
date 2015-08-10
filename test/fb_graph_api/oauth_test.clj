(ns fb-graph-api.oauth-test
  (:use org.httpkit.fake)
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
    (background
     (around :facts (with-fake-http ["https://graph.facebook.com/v2.4/access_token"
                                     "{\"access_token\":\"TOKEN\",\"token_type\":\"bearer\",\"expires_in\":5181690}"] ?form)))
    @(access-token ..app_id.. ..app_secret.. ..redirect_uri.. ..code..) =>
    {:access_token "TOKEN", :token_type "bearer", :expires_in 5181690})

  (fact "Exchanging incorrect code to user token returns error"
    (background
     (around :facts (with-fake-http ["https://graph.facebook.com/v2.4/access_token"
                                     {:status 500
                                      :body (str "{\"error\": {\"message\": \"ERROR\","
                                                 "\"type\": \"OAuthException\",\"code\": 100}}")}] ?form)))
    @(access-token ..app_id.. ..app_secret.. ..redirect_uri.. ..bad_code..) =>
      {:error {:code 100, :message "ERROR", :type "OAuthException"}})

  (fact "Connection error while exchanging data returns error"
    (background
     (around :facts (with-fake-http ["https://graph.facebook.com/v2.4/access_token"
                                     {:error (Exception. "Some error")}] ?form)))
    @(access-token ..app_id.. ..app_secret.. ..redirect_uri.. ..bad_code..) =>
    (just {:error anything})))

(facts "#request-app-only-token"
  (fact "Requesting app token using correct parameters"
    (background
     (around :facts (with-fake-http ["https://graph.facebook.com/v2.4/access_token"
                                     {:status 200
                                      :body "{\"access_token\":\"TOKEN\",\"token_type\":\"bearer\"}"}] ?form)))
    @(request-app-only-token ..app_id.. ..app_secret..) =>
    {:access_token "TOKEN", :token_type "bearer"})

  (fact "Exchanging incorrect code to user token throws exception"
    (background
     (around :facts (with-fake-http ["https://graph.facebook.com/v2.4/access_token"
                                     {:status 500
                                      :body (str "{\"error\": {\"message\": \"ERROR\","
                                                 "\"type\": \"OAuthException\",\"code\": 100}}")}] ?form)))
    @(request-app-only-token ..app_id.. ..app_secret..) =>
      {:error {:code 100, :message "ERROR", :type "OAuthException"}})

  (fact "Error while exchanging data throws exception"
    (background
     (around :facts (with-fake-http ["https://graph.facebook.com/v2.4/access_token"
                                     {:error (Exception. "Some error")}] ?form)))
    @(request-app-only-token ..app_id.. ..app_secret..) =>
      (just {:error anything})))
