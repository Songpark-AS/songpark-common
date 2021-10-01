(ns songpark.common.taxonomy.teleporter
  (:require [clojure.spec.alpha :as spec]
            [clojure.spec.gen.alpha :as gen]
            [songpark.common.taxonomy.mqtt]))

(def mac-regex #"^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
(def ip-regex #"\b(?:(?:2(?:[0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\.){3}(?:(?:2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9]))\b")

(spec/def :teleporter/id int?)
(spec/def :teleporter/voip string?)
(spec/def :teleporter/ip (spec/and string? #(re-matches ip-regex %)))
(spec/def :teleporter/ipv6 string?)
(spec/def :teleporter/mac (spec/and string? #(re-matches mac-regex %)))
(spec/def :teleporter/on boolean?)
(spec/def :teleporter/available boolean?)
(spec/def :teleporter/nickname string?)
(spec/def :teleporter/bits string?)
(spec/def :teleporter/uuid (spec/nilable uuid?))

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


(spec/def :teleporter/uuids
  (spec/coll-of (spec/keys :req [:teleporter/uuid])))
