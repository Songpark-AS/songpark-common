(ns songpark.common.protocol.message)

(defprotocol IMessageService
  (send-message! [this msg]))
