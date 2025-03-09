(ns dev.server.watch
  (:require [site.core]
            [dev.server.static :refer [static-start!]]
            [dev.server.hot-reload :refer [dev-start!]]))

(defn start! []
  (site.core/build!)
  (static-start!)
  (dev-start!))
