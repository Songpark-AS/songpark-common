(ns songpark.common.i18n
  (:require [re-frame.core :as rf]
            [tongue.core :as tongue]))

#?(:cljs (rf/reg-sub :locale/locale (fn [db [_ fallback]]
                                      (or (:locale/locale db) fallback))))

(defn init-i18n [data]
  ;; this is a bit hacky
  (def translate (tongue/build-translate data)))


#?(:cljs (def locale-sub (rf/subscribe [:locale/locale :en])))

(defonce show-placeholder? (atom false))

(defn t [& args]
  (if @show-placeholder?
    (let [[placeholder & _] args]
      (str placeholder))
    (apply translate @locale-sub args)))

(defn t-with-locale [locale & args]
  (if @show-placeholder?
    (let [[placeholder & _] args]
      (str placeholder))
    (apply translate locale args)))


#?(:cljs (rf/reg-event-fx :i18n/initialize (fn [_ [_ dictionary]]
                                             (init-i18n dictionary)
                                             nil)))

#?(:cljs (rf/reg-event-fx :locale/locale (fn [{:keys [db]} [_ locale]]
                                           {:db (assoc-in db [:locale/locale] locale)
                                            :dispatch [:locale/status :changed]})))

#?(:cljs (rf/reg-sub :locale/locale (fn [db _]
                                      (:locale/locale db))))
