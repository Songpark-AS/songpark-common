(ns common.mqtt.connection
  (:require [clojurewerkz.machine-head.client :as mh]))


(defn publish
  "Publishes code to mqtt server
   Expects a connection, a topic, and a map with :pointer and :arguments
   \"pointer\" should be a vector of keys, that specifies the position of a handler function in a clojure map
   Consider the following handler map:

   (def handler-map {:a {:c str :b +} :d -})

   The \"+\" function has the following pointer: [:a :b].
   it may be supplied the following arguments: [1 2 3]
   Result would be =6"
  [conn
   topic
   {pointer :pointer
    arguments :arguments
    :as input}]
  (mh/publish conn topic (str input)))

(defn subscribe
  "Subscribes to code on a mqtt server
   Expects a connection, a topic, and a handler-map that sorts all functions in a map"
  [conn topic handler-map]
  (mh/subscribe conn {topic 0} (fn [^String topic _ ^bytes payload]
                                          (let [payload (String. payload "UTF-8")
                                                {pointer :pointer
                                                 arguments :arguments
                                                 :as input} (clojure.core/read-string payload)]
                                            (println "here is result: " (apply (get-in handler-map pointer) arguments))))))

(comment "tests"
         
         (defn printor [statment] (println statment))
         (testor)
         (mh/publish conn "helloo" "Hello, world")
         
         (def conn (mh/connect "tcp://127.0.0.1:1883"))
         (def handler-map {:a {:c str
                               :b +}
                           :d -})
         
         (publish conn "helloo" {:pointer [:a :b]
                                 :arguments [1 2 3]})
         (subscribe conn "helloo" handler-map)
         
         
         )
         