(ns common.utility.communication
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(defn print-response-error
  [endpoint response-params]
  (println "Error in endpoint: " endpoint)
  (println "Error description returned by platform: " (:status-desc response-params)))

(defn platform-get-request
  "Creates simple HTTP GET request from platform"
  [endpoint parameters]
  (let [response (try
                   @(http/get endpoint {:query-params parameters}))
        response-body (:body response)
        response-params (if response-body
                          (if (= 400 (:status response))
                            (clojure.core/read-string (:body response))
                            (clojure.core/read-string (get (json/read-str (:body response)) "value")))
                         nil)]
    ; returns a map
    {:response response
     :response-params response-params}))