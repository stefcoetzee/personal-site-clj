(ns site.watch
  (:require [site.core]
            [site.server :as server]
            [dev.server.hot-reload :refer [dev-start!]]))

(defn start! []
  (site.core/build!)
  (server/static-start!)
  (dev-start!))
