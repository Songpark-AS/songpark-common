(ns songpark.common.config)

(def config (atom {:mqtt-settings {:host "127.0.0.1"
                                   :port 8000
                                   :client-id "app"
                                   }}))

#?(:cljs (def debug? goog.DEBUG))

(defn is? [path value]
  (= value (get-in @config path ::not-found)))
