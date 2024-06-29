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
        dir?     (fs/directory? (fs/path pub-dir (str/replace-first uri "/" "")))
        resource (fs/path pub-dir
                          (str (str/replace-first uri "/" "")
                               (if dir?
                                 "index.html"
                                 (when (nil? ext)
                                   ".html"))))]
    (println "serving:" req)
    (if (fs/exists? resource)
      {:status 200
       :headers {"Content-Type" (content-type resource)}
       :body    (fs/read-all-bytes resource)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Not found"})))

(comment
  (site {:uri "/to-and-sf"})

  (let [resource (fs/path "public/to-and-sf.html")
        ext "html"]
    (if (fs/exists? (fs/path "public/to-and-sf.html"))
      {:status 200
       :headers {"Content-Type" (content-type ext)}
       :body    (fs/read-all-bytes resource)}
      {:status  404
       :headers {"Content-Type" "text/plain"}
       :body    "Not found"}))

  (let [uri      "/to-and-sf"
        ext      (fs/extension uri)
        dir?     (fs/directory? (fs/path pub-dir (str/replace-first uri "/" "")))
        resource (fs/path pub-dir
                          (str (str/replace-first uri "/" "")
                               (if dir?
                                 "index.html"
                                 (when (nil? ext)
                                   ".html"))))]
   resource)

  (let [uri      "/to-and-sf"
        ext      (fs/extension uri)
        resource (fs/path pub-dir 
                          (str (str/replace-first uri "/" "")
                               (when (nil? ext)
                                 ".html")))]
    (fs/exists? resource)) 
  :rcf)

(comment
  (fs/directory?(fs/path pub-dir))
  (content-type "public/index.html") ; use HTML
  :rcf)

(defn start! [& [port & _args]]
  (let [port (or port 5000)]
    (println (str "Starting server at http://localhost:" port))
    (reset! server (s/run-server #'site {:port port}))))

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
   {:on-receive (fn [ch msg]
                  (swap! connected-clients conj ch)
                  (println "received:" msg)
                  (println "connected clients:" @connected-clients))
    :on-open    (fn [ch]
                  (swap! connected-clients conj ch)
                  (println "opened channel:" ch)
                  (println "connected clients:" @connected-clients))
    :on-close   (fn [ch status-code]
                  (swap! connected-clients disj ch)
                  (println "closing status:" status-code)
                  (println "connected clients:" @connected-clients))}))

(defn dev-start! []
  (reset! dev-server (s/run-server #'dev-handler {:port 5001}))
  (println "Dev server started at http://localhost:5001"))

(defn dev-stop! []
  (when-not (nil? @dev-server)
    (@dev-server :timeout 100)
    (reset! @dev-server nil)))

(defn broadcast [msg]
  (doseq [ch @connected-clients]
    (s/send! ch msg)))
