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


#_(defn reload-fn [event]
    (reload-namespaces "src/site")
    (site.core/build!)
    (server/restart!)
    (server/broadcast "reload")
    (prn event))


(defn reload-fn2 [event]
  (reload-namespaces "src/site")
  (site.core/build2!)
  (server/restart!)
  (server/broadcast "reload")
  (prn event))

#_(defn start! []
    (site.core/build!)
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

(defn start2! []
  (site.core/build2!)
  (fw/watch "src"
            reload-fn2
            {:delay-ms  50
             :recursive true})
  (fw/watch "content"
            reload-fn2
            {:delay-ms  50
             :recursive true})
  (server/start!)
  (server/dev-start!)
  (println "Watching..."))
