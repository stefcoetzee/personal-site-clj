(ns site.watch
  (:require [site.core]
            [site.server :as server]
            [babashka.fs :as fs]))

(require '[babashka.pods :as pods])
(pods/load-pod 'org.babashka/fswatcher "0.0.5")

(require '[pod.babashka.fswatcher :as fw])

(defn reload-namespaces [dir]
  (let [clj-files (fs/glob dir "**.clj")]
    (doseq [file clj-files]
      (let [ns-name (->> file
                         (fs/file-name)
                         (fs/strip-ext)
                         (str "site.")
                         (symbol))]
        (println "Reloading namespace:" ns-name)
        (require ns-name :reload)))))

(comment
  (let [clj-files (fs/glob "src/site" "**.clj")]
    (for [clj-file clj-files]
      (namespace clj-file)))
  
  (reload-namespaces "src/site")
  :rcf)

(defn reload-fn [event]
  #_(require 'site.core :reload-all)
  (do
    (reload-namespaces "src/site")
    (site.core/build)
    (server/restart!)
    (server/broadcast "reload")
    (prn event)))

(defn start! []
  (site.core/build)
  (fw/watch "content"
            reload-fn
            {:delay-ms  200
             :recursive true})
  (fw/watch "src"
            reload-fn
            {:delay-ms  200
             :recursive true})
  (server/start!)
  (server/dev-start!)
  (println "Watching..."))
