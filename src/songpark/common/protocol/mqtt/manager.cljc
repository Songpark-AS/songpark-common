(ns songpark.common.protocol.mqtt.manager
  (:require [taoensso.timbre :as log]
            #? (:clj [cognitect.transit :as transit])))

(defprotocol IMqttManager
  (subscribe [this topics])
  (unsubscribe [this topics])
  (publish! [this topic msg]))
