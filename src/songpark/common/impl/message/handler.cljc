(ns songpark.common.impl.message.handler
  (:require [taoensso.timbre :as log]))


(defmulti default-incoming :message/type)

(defmethod default-incoming :debug/info [{:keys [message/topic message/body]}]
  (log/debug ::default-incoming.debug [topic body]))

(defmethod default-incoming :default [message]
  (let [msg-type (:message/type message)]
    (throw (ex-info (str "No message handler exist for message type " msg-type) message))))



(defmulti default-outgoing :message/type)

(defmethod default-outgoing :default [{:message/keys [type] :as message}]
  (throw
   (ex-info (str "No message handler defined for message type " type) message)))



