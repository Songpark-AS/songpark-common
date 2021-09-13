(ns songpark.common.taxonomy.teleporter
  (:require [clojure.spec.alpha :as spec]
            [clojure.spec.gen.alpha :as gen]
            [songpark.common.taxonomy.mqtt]))


(spec/def :teleporter/id int?)
(spec/def :teleporter/voip string?)
(spec/def :teleporter/ip string?)
(spec/def :teleporter/on boolean?)
(spec/def :teleporter/available boolean?)
(spec/def :teleporter/nickname string?)
(spec/def :teleporter/bits string?)
(spec/def :teleporter/uuid (spec/nilable string?))

(spec/def :teleporter/teleporter
  (spec/keys :req [:teleporter/voip
                   :teleporter/ip
                   :teleporter/on
                   :teleporter/available
                   :teleporter/nickname
                   :teleporter/bits
                   :teleporter/uuid]
             :opt [:teleporter/id]))

(spec/def :teleporter/connect
  (spec/keys :req [:teleporter/nickname
                   :teleporter/bits
                   :teleporter/uuid
                   :mqtt/username
                   :mqtt/password]))
