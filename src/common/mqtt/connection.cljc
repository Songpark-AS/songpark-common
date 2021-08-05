(ns common.mqtt.connection
  (:require [clojurewerkz.machine-head.client :as mh]))


(defn init
  "This function initiates a simple mqtt connection using machine head.
   Expects a topic name.
   Uses default URI: \"tcp://127.0.0.1:1883\", unless other URI is specified
   Example uses:
   
   (init \"MY-TOPIC\")
   (init \"MY-TOPIC\" \"URI\")

   Returns a connection-map on format:
   
   {:connection CONNECTION
    :topic TOPIC}"
  
  ([topic uri]
   (let [connection (mh/connect uri)]
     {:connection connection
      :topic topic}))
  ([topic]
   (init topic "tcp://127.0.0.1:1883")))


(defn publish
  "This function publishes an instruction to the mqtt server.
   Expects a connection-map and a vector of pointers and arguments
   Or, expects a mh-connection, a topic-name, and a vector of pointers and arguments.
   \"pointer\" should be a vector of keys, that specifies the position of a handler function in a clojure map
   Consider the following handler map:

   (def handler-map {:a {:c str 
                         :b +} 
                     :d -})

   The \"+\" function has the following pointer: [:a :b].
   it may be supplied the following arguments: [1 2 3]
   Result would be =6
   Example uses:
   
   (publish MY-CONNECTION MY-TOPIC [[:a :b][1 2 3]])
   (publish CONNECTION-MAP [[:a :b][1 2 3]])"
  ([connection topic input-map]
  (mh/publish connection topic (str input-map)))
  
  ([connection-map [pointer arguments]]
   (publish (:connection connection-map) (:topic connection-map) {:pointer pointer :arguments arguments})))


(defn subscribe
  "Subscribes to a topic on an mqtt server, and waits for instructions.
   Expects a mh-connection, a topic-name, and a handler-map.
   Example use:
   
   (subscribe CONNECTION TOPIC HANDLER-MAP)
   (subscribe CONNECTION-MAP HANDLER-MAP)"
  ([connection topic handler-map]
  (mh/subscribe connection {topic 0} (fn [^String topic _ ^bytes payload]
                                          (let [payload (String. payload "UTF-8")
                                                {pointer :pointer
                                                 arguments :arguments} (clojure.core/read-string payload)]
                                            (apply (get-in handler-map pointer) arguments)))))
  ([{connection :connection
     topic :topic}
    handler-map]
   (subscribe connection topic handler-map)))


(comment "testing area"

         (let [conn-map (init "topico")
               handler-map {:vanlig-op {:minus #(println (apply - %&))
                                        :pluss #(println (apply + %&))}
                            :uvanlig-op {:gange #(println (apply * %&))
                                         :dele #(println (apply / %&))}}]
           
           (subscribe conn-map handler-map)

           (publish conn-map [[:vanlig-op :pluss][]])
           (publish conn-map [[:vanlig-op :minus] [1 2 3 4]])
           (publish conn-map [[:vanlig-op :pluss] [1 2 3 5]])
           (publish conn-map [[:uvanlig-op :gange] [1 2]])
           (publish conn-map [[:uvanlig-op :dele] [16 2]])))
         