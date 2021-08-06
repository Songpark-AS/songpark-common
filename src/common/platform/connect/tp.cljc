(ns common.platform.connect.tp
  (:require [common.utility.communication :as communication]))

(defn init 
  "This function should be used by a teleporter to tell the platform that it is turned on.  
   The function expects a map with a tpid. Specified endpoint is optional (default: \"http://localhost:3000/connect/tp/init\")  
   
   Example uses:
    (init {:tpid \"1010\"})
    (init {:tpid \"1010\"
           :endpoint ENDPOINT})

   Returns:
    {:status STATUS
     :status-desc STATUS-DESCRIPTION
     :uuid UUID}"
  [parameters]
  (let [{endpoint :endpoint :or {endpoint "http://localhost:3000/connect/tp/init"}} parameters
        {response :response
         response-params :response-params} (communication/platform-get-request endpoint parameters)]
    (if (:status response-params)
      #?(:clj response-params ;Returns a clojure map if clojure
         :cljs)
      (communication/print-response-error endpoint response-params))))


(defn disconnect
  "This function should be used to tell the platform that the tp has disconnected and is available for a new connection.    
   The function expects a map with a tpid. Specified endpoint is optional (default: \"http://localhost:3000/connect/tp/disconnect\")  
   
   Example uses:  
    (disconnect {:tpid \"0100\"})
    (disconnect {:tpid \"0100\"
                 :endpoint ENDPOINT})
   Returns:
    {:status STATUS
     :status-desc STATUS-DESCRIPTION
     :uuid UUID}"
  [parameters]
  (let [{endpoint :endpoint :or {endpoint "http://localhost:3000/connect/tp/disconnect"}} parameters
        {response :response
         response-params :response-params} (communication/platform-get-request endpoint parameters)]
    (if (:status response-params)
      #?(:clj response-params ;Returns a clojure map if clojure
         :cljs)
      (communication/print-response-error endpoint response-params))))


(defn turnoff
  "This function should be used to tell the platform that the tp is turned off and unavailable.  
   The function expects a map with a tpid. Specified endpoint is optional (default: \"http://localhost:3000/connect/tp/turnoff\")  
   
   Example uses:  
    (turnoff {:tpid \"0100\"})
    (turnoff {:tpid \"0100\"
              :endpoint ENDPOINT})
   Returns:
    {:status STATUS
     :status-desc STATUS-DESCRIPTION}"
  [parameters]
  (let [{endpoint :endpoint :or {endpoint "http://localhost:3000/connect/tp/turnoff"}} parameters
        {response :response
         response-params :response-params} (communication/platform-get-request endpoint parameters)]
    (if (:status response-params)
      #?(:clj response-params ;Returns a clojure map if clojure
         :cljs)
      (communication/print-response-error endpoint response-params))))
