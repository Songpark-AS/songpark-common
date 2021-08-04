(ns common.mqtt.connection
  (:require [clojurewerkz.machine-head.client :as mh]))


(defn init
  "Initiates a simple mqtt connection using machine head.
   Expects a topic name.
   Uses default URI: \"tcp://127.0.0.1:1883\", unless other URI is specified
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
  "Publishes code to mqtt server
   Expects a connection, a topic, and a map with :pointer and :arguments
   \"pointer\" should be a vector of keys, that specifies the position of a handler function in a clojure map
   Consider the following handler map:

   (def handler-map {:a {:c str 
                         :b +} 
                     :d -})

   The \"+\" function has the following pointer: [:a :b].
   it may be supplied the following arguments: [1 2 3]
   Result would be =6
   Example uses:
   
   (publish my-connection my-topic {:pointer [:a :b] :arguments [1 2 3]})
   (publish connection-map [[:a :b][1 2 3]])"
  ([connection topic input-map]
  (mh/publish connection topic (str input-map)))
  
  ([connection-map [pointer arguments]]
   (publish (:connection connection-map) (:topic connection-map) {:pointer pointer :arguments arguments})))


(defn subscribe
  "Subscribes to code on a mqtt server
   Expects a connection, a topic, and a handler-map that sorts all functions in a map.
   Example use:
   
   (subscribe connection topic handler-map)
   (subscribe connection-map handler-map)"
  ([connection topic handler-map]
  (mh/subscribe connection {topic 0} (fn [^String topic _ ^bytes payload]
                                          (let [payload (String. payload "UTF-8")
                                                {pointer :pointer
                                                 arguments :arguments
                                                 :as input} (clojure.core/read-string payload)]
                                            (apply (get-in handler-map pointer) arguments)))))
  ([{connection :connection
     topic :topic}
    handler-map]
   (subscribe connection topic handler-map)))

(comment "testing area"

         (let [conn-map (init "topico")
               handler-map {:a {:a #(println (apply - %&))
                                :b #(println (apply + %&))}
                            :c {:b #(println (apply * %&))
                                :c #(println (apply / %&))}}]
           (subscribe conn-map handler-map)
           (println (publish conn-map [[:a :b][1 2 3]]))
           (publish conn-map [[:a :a] [1 2 3 4]])
           (publish conn-map [[:a :b] [1 2 3 5]])
           (publish conn-map [[:c :b] [1 2]])
           (publish conn-map [[:c :c] [16 2]]))
         )
         