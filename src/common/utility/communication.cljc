(ns common.utility.communication
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(defn print-response-error
  [endpoint response-params]
  (println "Error in endpoint: " endpoint)
  (println "Error description returned by platform: " (:status-desc response-params)))

(defn platform-get-request
  "Creates simple HTTP GET request from platform"
  [{endpoint :endpoint :as all-params}]
  (let [response (try
                   @(http/get endpoint {:query-params all-params}))
        response-body (:body response)
        response-params (if response-body
                         (read-string (get (json/read-str (:body response)) "value"))
                         nil)]
    ; returns a map
    {:response response
     :response-body response-body
     :response-params response-params}))