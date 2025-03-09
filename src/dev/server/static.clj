(ns dev.server.static
  (:require [clojure.java.io]
            [clojure.string :as str]
            [babashka.fs :as fs]
            [org.httpkit.server :as s]
            [site.core :refer [pub-dir]]))

(defonce static-server (atom nil))

(defn content-type [path]
  (let [ext (fs/extension path)]
    (condp = ext
      "html" "text/html"
      "js" "application/javascript"
      "css" "text/css"
      "text/html")))

(defn static-handler [req]
  (let [uri      (:uri req)
        ext      (fs/extension uri)
        dir?
        (fs/directory? (fs/path pub-dir (str/replace-first uri "/" "")))
        resource
        (fs/path pub-dir (str (str/replace-first uri "/" "")
                              (if dir?
                                "index.html"
                                (when (nil? ext) ".html"))))]
    (println "serving:" uri)
    (if (fs/exists? resource)
      {:status 200
       :headers {"Content-Type" (content-type resource)}
       :body    (fs/read-all-bytes resource)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Not found"})))

(defn static-start! [& [port & _args]]
  (let [port (or port 5000)]
    (reset! static-server (s/run-server #'static-handler {:port port}))
    (println (str "Static-site server listening on http://localhost:" port))))

(defn static-stop! []
  (when-not (nil? @static-server)
    (@static-server :timeout 100)
    (reset! static-server nil)))

(defn static-restart! []
  (static-stop!)
  (static-start!))

(comment
  (static-start!)
  (static-stop!)
  :rcf)
