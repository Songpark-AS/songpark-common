(ns songpark.common.protocols.mqtt
  (:require [taoensso.timbre :as log]))

(defprotocol IMqtt
  (connect [this])
  (is-connected? [this])
  (publish! [this topic message])
  (disconnect [this])
  (subscribe [this topic on-message])
  (unsubscribe [this topic]))

(extend-protocol IMqtt
  nil
  (connect [this] (log/warn "Tried MQTT connect with nil"))
  (publish! [this topic message] (log/warn "Tried MQTT send with nil")))
