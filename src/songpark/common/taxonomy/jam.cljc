(ns songpark.common.taxonomy.jam
  (:require [clojure.spec.alpha :as spec]))


(spec/def :jam/id uuid?)
(spec/def :jam/status boolean?)

(spec/def :jam/response (spec/keys :req [:jam/status
                                         :jam/id]))
