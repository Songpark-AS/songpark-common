(defproject songpark/common "0.1.1-SNAPSHOT"

  :description "Songpark common library"

  :dependencies [[org.clojure/clojure "1.10.3" :scope "provided"]
                 [org.clojure/clojurescript "1.10.879" :scope "provided"]
                 ;; web
                 ;; reagent was here
                 ;; we want to have dev building within this library however,
                 ;; and that is easiest done with figwheel
                 ;; we use shadow-cljs everywhere else though as it has
                 ;; superior interop with the npm ecosystem
                 ;; so do a local release with lein with-profile build install
                 ;; and use things as is for dev work

                 ;; wiring (forms)
                 [ez-wire "0.5.0-beta3"]

                 ;; matching
                 [org.clojure/core.match "1.0.0"]

                 ;; i18n
                 [tongue "0.3.0"]

                 ;; structure
                 [re-frame "1.2.0"]
                 [com.stuartsierra/component "1.0.0"]

                 ;; communication
                 [cljs-ajax "0.8.3"]
                 [tick "0.5.0-RC1"]
                 [clojurewerkz/machine_head "1.0.0"]
                 
                 ;; data format
                 [com.cognitect/transit-clj "1.0.324"]
                 [com.cognitect/transit-cljs "0.8.269"]
                 [cheshire "5.10.0"]

                 ;; logging
                 [com.taoensso/timbre "5.1.2"]
                 [com.fzakaria/slf4j-timbre "0.3.21"]
                 [raven-clj "1.6.0"]

                 ;;config
                 [cprop "0.1.11"]

                 ;;filesystem
                 [me.raynes/fs "1.4.6"]]

  :repl-options {:init-ns songpark.core}

  :main songpark.common.core
  :aot [songpark.common.core]

  :min-lein-version "2.5.3"

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles
  {:build
   {:source-paths ["src"]
    :dependencies [[reagent "1.1.0" :exclusions [cljsjs/react
                                                 cljsjs/react-dom
                                                 cljsjs/react-dom-server
                                                 cljsjs/create-react-class]]]}
   :dev
   {:source-paths ["src" "dev"]
    :dependencies [[binaryage/devtools "1.0.3"]
                   [day8.re-frame/re-frame-10x "1.1.13"]
                   [day8.re-frame/tracing "0.6.2"]
                   [reagent "1.1.0"]]

    :plugins [[lein-cljsbuild "1.1.8"]
              [lein-figwheel "0.5.20"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src" "dev"]
     :figwheel     {:on-jsload "songpark.dev/mount-root"}
     :compiler     {:main                 songpark.dev
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "/js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}}}]})
