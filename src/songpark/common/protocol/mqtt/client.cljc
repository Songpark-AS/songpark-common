(ns songpark.common.protocol.mqtt.client
  (:require [taoensso.timbre :as log]
            #? (:clj [cognitect.transit :as transit])))

(defprotocol IMqttClient
  (connect [this])
  (connected? [this])
  (publish [this topic message])
  (disconnect [this])
  (subscribe [this topic on-message])
  (unsubscribe [this topic]))

(extend-protocol IMqttClient
  nil
  (connect [this] (log/warn "Tried MQTT connect with nil"))
  (publish [this topic message] (log/warn "Tried MQTT send with nil")))
