(ns common.platform.connect.client
  (:require [common.utility.communication :as communication]))


(defn init
  "This function should be used by the client (i.e. the phone app) to request a connection to a specific tp. 
   The function expects a map with a nickname. Specified endpoint is optional (default: \"http://localhost:3000/connect/client/init\")
   
   Example uses:
    (init {:nickname \"christians.dream\"})
    (init {:nickname \"christians.hope\"
           :endpoint ENDPOINT})

   Returns:
    {:status STATUS
     :status-desc STATUS-DESCRIPTION
     :uuid UUID}"
  [parameters]
  (let [{endpoint :endpoint :or {endpoint "http://localhost:3000/connect/client/init"}} parameters
        {response :response
         response-params :response-params} (communication/platform-get-request endpoint parameters)]
    (if (:status response-params)
      #?(:clj response-params ;Returns a clojure map if clojure
         :cljs)
      (communication/print-response-error endpoint response-params))))

