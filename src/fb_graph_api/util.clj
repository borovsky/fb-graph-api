(ns fb-graph-api.util
  (:require [clojure
             [pprint :refer [pprint]]
             [string :as str]]
            [clojure.data.json :as json]
            [org.httpkit.client :as http]))

(def graph-root "https://graph.facebook.com/v2.4/")

(defn graph-url
  [parts]
  (str graph-root (str/join "/" parts)))

(defn response-processor
  [promise]
  (fn [{:keys [status headers body error] :as resp}]
    (deliver promise
             (if error
               {:error {:exception error}}
               (json/read-str body :key-fn keyword)))))

(defn load-graph-data
  [url params]
  (pprint [url params])
  (let [promise (promise)
        response(http/get url
                          {:query-params params}
                          (response-processor promise))]
    promise))

(defmacro def-graph-get-request
  [fn-name path docstring]
  (let [keyword2symbol (comp symbol name)
        path-params (mapv keyword2symbol (filter keyword? path))
        params (into ['token] path-params)
        url-param (mapv #(if (keyword? %) (keyword2symbol %) %) path)
        fn-name-sym (symbol fn-name)]
    (list 'clojure.core/defn fn-name-sym docstring
          (list params (cons fn-name-sym (conj params {})))
          (list (conj params 'params)
                (list 'clojure.core/let
                      ['url (list 'fb-graph-api.util/graph-url url-param)]
                      '(fb-graph-api.util/load-graph-data
                        url (clojure.core/merge params {:access_token token}))))))
  )

