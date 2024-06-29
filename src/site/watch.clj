(ns site.watch
  (:require [site.core]
            [site.server :as server]))

(require '[babashka.pods :as pods])
(pods/load-pod 'org.babashka/fswatcher "0.0.5")

(require '[pod.babashka.fswatcher :as fw])

(defn start! [] 
  (site.core/-main)
  (fw/watch "content"
            (fn [event]
              (site.core/-main)
              (server/restart!)
              (server/broadcast "reload")
              (prn event))
            {:delay-ms  200
             :recursive true})
  (server/start!)
  (server/dev-start!)
  (println "Watching...")
  (deref (promise)))
