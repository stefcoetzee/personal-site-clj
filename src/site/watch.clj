(ns site.watch
  (:require [site.core]
            [site.server :as server]))

(require '[babashka.pods :as pods])
(pods/load-pod 'org.babashka/fswatcher "0.0.5")

(require '[pod.babashka.fswatcher :as fw])

(defn reload-fn [event]
  (require 'site.core :reload-all)
  (site.core/build)
  (server/restart!)
  (server/broadcast "reload")
  (prn event))

(defn start! []
  (site.core/build)
  (fw/watch "content"
            reload-fn
            {:delay-ms  100
             :recursive true})
  (fw/watch "src"
            reload-fn
            {:delay-ms  100
             :recursive true})
  (server/start!)
  (server/dev-start!)
  (println "Watching..."))
