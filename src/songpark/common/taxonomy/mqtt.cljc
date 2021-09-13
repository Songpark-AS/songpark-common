(ns songpark.common.taxonomy.mqtt
  (:require [clojure.spec.alpha :as spec]))


(spec/def :mqtt/id int?)
(spec/def :mqtt/username string?)
(spec/def :mqtt/password string?)

(spec/def :mqtt/mqtt
  (spec/keys :req [:mqtt/username
                   :mqtt/password]
             :opt [:mqtt/id]))
