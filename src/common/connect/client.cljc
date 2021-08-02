(ns common.connect.client
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [common.utility.communication :as communication]))
;; This is relative path:
;; src/common/connect/client.cljc

(defn init
  "This endpoint should be used by the client (i.e. the phone app) to request a connection to a specific tp.
   The platform expects nickname to be tagged on as a parameter. The parameter should be named \"nickname\".
   Specified endpoint is optional. Example uses:

   (init {:nickname \"christians.dream\"})
   (init {:nickname \"christians.hope\"
          :endpoint ENDPOINT})
   "
  [parameters]
  (let [{endpoint :endpoint :or {endpoint "http://localhost:3000/connect/client/init"}} parameters
        {response-params :response-params} (communication/platform-get-request (assoc parameters :endpoint endpoint))]
    (if (:status response-params)
      #?(:clj response-params ;Returns a clojure map if clojure
         :cljs)
      (communication/print-response-error endpoint response-params))))
