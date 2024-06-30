(ns site.server
  (:require [clojure.java.io]
            [clojure.string :as str]
            [babashka.fs :as fs]
            [org.httpkit.server :as s]
            [site.core :refer [pub-dir]]))

(defonce server (atom nil))

(defonce dev-server (atom nil))

(defonce connected-clients (atom #{}))

(defn content-type [path]
  (let [ext (fs/extension path)]
    (condp = ext
      "html" "text/html"
      "js" "application/javascript"
      "css" "text/css"
      "text/html")))

(defn site [req]
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

(defn start! [& [port & _args]]
  (let [port (or port 5000)]
    (reset! server (s/run-server #'site {:port port}))
    (println (str "Static-site server listening on http://localhost:" port))))

(defn stop! []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn restart! []
  (stop!)
  (start!))

(comment
  (start!)
  (stop!)
  :rcf)

(defn dev-handler [req]
  (s/as-channel
   req
   {:on-receive (fn [ch _msg] (swap! connected-clients conj ch))
    :on-open    (fn [ch] (swap! connected-clients conj ch))
    :on-close   (fn [ch _status-code] (swap! connected-clients disj ch))}))

(defn dev-start! []
  (reset! dev-server (s/run-server #'dev-handler {:port 5001}))
  (println "Dev server listening on http://localhost:5001"))

(defn dev-stop! []
  (when-not (nil? @dev-server)
    (@dev-server :timeout 100)
    (reset! @dev-server nil)))

(defn broadcast [msg]
  (doseq [ch @connected-clients]
    (s/send! ch msg)))
