(ns songpark.common.communication
  (:require [ajax.core :as ajax]
            [ajax.interceptors :as ajax-interceptors]
            [ajax.transit :as ajax-transit]
            [clojure.string :as str]
            [cognitect.transit :as transit]
            #?(:cljs [java.time])
            #?(:cljs [re-frame.core :as rf])
            [tick.core :as t]
            [taoensso.timbre :as log]))

#? (:cljs (defn get-cookie [cookie-name]
            ;; check that cookie does exist
            (if (and (.-cookie js/document) (not= "" (.-cookie js/document)))
              ;; split cookie into all the cookies
              (let [cookies (str/split (.-cookie js/document) ";")]
                ;; reduce over the cookies until we find the one we want. could do a loop, but there
                ;; will never be enough cookies to make the extra effort worth it
                (reduce (fn [out cookie]
                          (if (= (str cookie-name "=") (.substring cookie 0 (inc (count cookie-name))))
                            (js/decodeURIComponent (.substring cookie (inc (count cookie-name))))
                            out))
                        nil (->> cookies
                                 (remove nil?)
                                 (map str/trim))))
              ;; return nil if we found nothing
              nil)))

#? (:cljs (defn csrf-token-cookie [headers]
            (if-let [token (get-cookie "xsrftoken")]
              (assoc headers "X-CSRFToken" token)
              headers)))

#? (:cljs (defn massage-headers [headers]
            (-> headers
                #_csrf-token-cookie))
    :clj (defn massage-headers [_ headers]
           (-> headers)))

(def credentials (atom nil))
(def base-url (atom nil))

(defn time-fn [obj]
  (str obj))
(defn rep [text]
  (fn [& _]
    text))

#? (:cljs (def write-handlers {java.time/Instant (transit/write-handler (rep "time/instant") time-fn)
                               java.time/Month (transit/write-handler (rep "time/month") time-fn)
                               java.time/DayOfWeek (transit/write-handler (rep "time/day-of-week") time-fn)
                               java.time/Year (transit/write-handler (rep "time/year") time-fn)})
    :clj  (def write-handlers {java.time.Instant (transit/write-handler (rep "time/instant") time-fn)
                               java.time.Month (transit/write-handler (rep "time/month") time-fn)
                               java.time.DayOfWeek (transit/write-handler (rep "time/day-of-week") time-fn)
                               java.time.Year (transit/write-handler (rep "time/year") time-fn)}))

#? (:cljs (def writer (transit/writer :json {:handlers write-handlers}))
    :clj  (def writer (transit/writer (java.io.ByteArrayOutputStream. 4096) :json {:handlers write-handlers})))

(def read-handlers {"time/instant" (transit/read-handler (fn [obj] (t/instant obj)))
                    "time/month" (transit/read-handler (fn [obj] (t/month obj)))
                    "time/day-of-week" (transit/read-handler (fn [obj] (t/day-of-week obj)))
                    "time/year" (transit/read-handler (fn [obj] (t/year obj)))})

(def response-format
  (ajax-interceptors/map->ResponseFormat
   {:content-type ["application/transit+json"]
    :description "Transit response"
    :read (ajax-transit/transit-read-fn {:handlers read-handlers})}))

(defn- encode-get-params [params]
  (reduce (fn [out [k v]]
            (if (keyword? k)
              (let [k (-> k
                          str
                          (subs 1)
                          #? (:cljs (js/encodeURIComponent)
                              :clj (java.net.URLEncoder/encode)))]
                (assoc out k v))
              (assoc out k v)))
          {} params))

#? (:cljs (defn- get-handler+error-handler [chained-success chained-error ?context]
            (let [success (cond (vector? chained-success)
                                (fn [data]
                                  (rf/dispatch (into chained-success [data ?context])))

                                ;; this is to support backward compatible behaviour
                                (and (map? chained-success)
                                     (nil? chained-error))
                                (:handler chained-success)

                                (keyword? chained-success)
                                (fn [data]
                                  (rf/dispatch [chained-success data ?context]))

                                :else
                                nil)
                  error (cond (vector? chained-error)
                              (fn [data]
                                (rf/dispatch (into chained-error [data ?context])))

                              ;; this is to support backward compatible behaviour
                              (and (map? chained-success)
                                   (nil? chained-error))
                              (:error chained-success)

                              (keyword? chained-error)
                              (fn [data]
                                (rf/dispatch [chained-error data ?context]))

                              :else
                              nil)]
              [success error])))

#? (:cljs (defn- event-fx-request-map [params chained-success chained-error ?context]
            (let [[handler error-handler] (get-handler+error-handler chained-success chained-error ?context)]
              (update
               (merge {:params params
                       :writer writer
                       :with-credentials true
                       :response-format response-format}
                      (when handler
                        {:handler handler})
                      (when error-handler
                        {:error-handler error-handler}))
               :headers massage-headers @credentials))))

#? (:cljs (defn- manual-request-map [params handler error-handler]
            (update
             (merge {:params params
                     :writer writer
                     :with-credentials true
                     :response-format response-format}
                    (when handler
                      {:handler handler})
                    (when error-handler
                      {:error-handler error-handler}))
             :headers massage-headers @credentials))

    :clj  (defn- manual-request-map [params handler error-handler]
            (update
             (merge {:params params
                     :writer writer
                     :type :json
                     :handlers write-handlers
                     :with-credentials true
                     :response-format response-format}
                    (when handler
                      {:handler handler})
                    (when error-handler
                      {:error-handler error-handler}))
             :headers massage-headers @credentials)))




(defn GET
  ([uri params] (GET uri params nil nil))
  ([uri params handler] (GET uri params handler nil))
  ([uri params handler error-handler]
   (ajax/GET (str @base-url uri)
             (manual-request-map (encode-get-params params)
                                 handler
                                 error-handler))))

(defn POST
  ([uri params] (POST uri params nil nil))
  ([uri params handler] (POST uri params handler nil))
  ([uri params handler error-handler]
   (ajax/POST (str @base-url uri)
              (manual-request-map params handler error-handler))))

(defn PUT
  ([uri params] (PUT uri params nil nil))
  ([uri params handler] (PUT uri params handler nil))
  ([uri params handler error-handler]
   (ajax/PUT (str @base-url uri)
             (manual-request-map params handler error-handler))))

(defn DELETE
  ([uri params] (DELETE uri params nil nil))
  ([uri params handler] (DELETE uri params handler nil))
  ([uri params handler error-handler]
   (ajax/DELETE (str @base-url uri)
                (manual-request-map params handler error-handler))))

#? (:cljs (rf/reg-event-fx :http/get (fn [_ [_ uri params chained-success chained-error ?context]]
                                       (ajax/GET (str @base-url uri)
                                                 (event-fx-request-map (encode-get-params params) chained-success chained-error ?context))
                                       nil)))

#? (:cljs (rf/reg-event-fx :http/post (fn [_ [_ uri params chained-success chained-error ?context]]
                                        (ajax/POST (str @base-url uri)
                                                   (event-fx-request-map params chained-success chained-error ?context))
                                        nil)))

#? (:cljs (rf/reg-event-fx :http/put (fn [_ [_ uri params chained-success chained-error ?context]]
                                       (ajax/PUT (str @base-url uri)
                                                 (event-fx-request-map params chained-success chained-error ?context))
                                       nil)))

#? (:cljs (rf/reg-event-fx :http/delete (fn [_ [_ uri params chained-success chained-error ?context]]
                                          (ajax/DELETE (str @base-url uri)
                                                       (event-fx-request-map params chained-success chained-error ?context))
                                          nil)))
