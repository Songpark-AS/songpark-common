(ns common.connect.tp
  (:require [common.utility.communication :as communication]))

(defn init 
  "This endpoint should be used by a teleporter to tell the platform that it is turned on and available on the mqtt network. 
   The platform expects tpid to be tagged on as a parameter. The parameter should be named \"tpid\".
   Specified endpoint is optional. Example uses:

   (init {:tpid 1010})
   (init {:tpid \"all\"})
   (init {:tpid \"all\"
          :endpoint ENDPOINT})

   Use \"tpid=all\" to affect all teleporters in the database."
  [parameters]
  (let [{endpoint :endpoint :or {endpoint "http://localhost:3000/connect/tp/init"}} parameters
        {response-params :response-params} (communication/platform-get-request (assoc parameters :endpoint endpoint))]
    (if (:status response-params)
      #?(:clj response-params ;Returns a clojure map if clojure
         :cljs)
      (communication/print-response-error endpoint response-params))))

(defn disconnect
  "This endpoint should be used to tell the db that the tp is available for a new connection. 
   This endpoint sets the following value for a teleporter available_status=true. This endpoint expects a parameter \"tpid\".
   Specified endpoint is optional. Example uses:
   
   (disconnect {:tpid 0100})
   (disconnect {:tpid \"all\"})
   (init {:tpid \"all\"
          :endpoint ENDPOINT})

   Use \"tpid=all\" to affect all teleporters in the database."
  [parameters]
  (let [{endpoint :endpoint :or {endpoint "http://localhost:3000/connect/tp/disconnect"}} parameters
        {response-params :response-params} (communication/platform-get-request (assoc parameters :endpoint endpoint))]
    (if (:status response-params)
      #?(:clj response-params ;Returns a clojure map if clojure
         :cljs)
      (communication/print-response-error endpoint response-params))))


(defn turnoff
  "This endpoint should be used to tell the db that a tp is turned off.
   This endpoint sets the following values for a teleporter available_status=false and on_status=false.
   This endpoint expects a parameter \"tpid\". Specified endpoint is optional. Example uses:
   
   (turnoff {:tpid 0100})
   (turnoff {:tpid \"all\"})
   (init {:tpid \"all\"
          :endpoint ENDPOINT})

   Use \"tpid=all\" to affect all teleporters in the database."
  [parameters]
  (let [{endpoint :endpoint :or {endpoint "http://localhost:3000/connect/tp/turnoff"}} parameters
        {response-params :response-params} (communication/platform-get-request (assoc parameters :endpoint endpoint))]
    (if (:status response-params)
      #?(:clj response-params ;Returns a clojure map if clojure
         :cljs)
      (communication/print-response-error endpoint response-params))))
