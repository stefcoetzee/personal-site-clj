(ns dev.server.hot-reload
  (:require [babashka.fs :as fs]
            [org.httpkit.server :as s]
            [dev.server.static :refer [static-restart!]]
            [site.core :refer [build!]]))

;; Hot reload (CLJ)

(defn last-modified [dirs]
  (let [dirs      (if (sequential? dirs) dirs [dirs])
        all-files (mapcat #(fs/glob % "**{.clj,.cljs,.cljc,.html,.css,.js,.md}")
                          dirs)
        last-modified-times (map #(fs/last-modified-time %)
                                 all-files)]
    (if (seq last-modified-times)
      (apply max (map #(.toMillis %) last-modified-times))
      0)))

(comment
  (last-modified ["src"])
  :rcf)

(defn modified? [last-timestamp dirs]
  (not= (Long/parseLong last-timestamp) (last-modified dirs)))

(defn has-changes
  ([req dirs]
   (has-changes req dirs 0))
  ([req dirs loop-count]
   (let [last-timestamp (get-in req [:query-params "last-modified"] "0")]
     (cond (= 30 loop-count)
           {:status 200
            :body (str last-timestamp)}

           (modified? last-timestamp dirs)
           {:status 200
            :body (str (last-modified dirs))}

           :else
           (do (Thread/sleep 200)
               (recur req dirs (inc loop-count)))))))

(defn create-hot-reload-handler [watch-dirs]
  (fn [req]
    (has-changes req watch-dirs)))

;; Reload namespaces

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

;; Server

(def last-build-time (atom 0))

(defn rebuild-if-needed! []
  (let [current-time (System/currentTimeMillis)
        latest-change (last-modified ["src" "content"])]
    (when (> latest-change @last-build-time)
      (println "File change detected")
      (Thread/sleep 300) ; debounce time [ms]
      (reload-namespaces "src/site")
      (reset! last-build-time current-time)
      (build!)
      (static-restart!))))

(defn dev-handler [req]
  (let [uri                (:uri req)
        watch-dirs         ["src" "content"]
        hot-reload-handler (create-hot-reload-handler watch-dirs)]
    (rebuild-if-needed!)
    (cond
      (= uri "/hot-reload")
      (-> (hot-reload-handler req)
          (assoc :headers {"Access-Control-Allow-Origin" "http://localhost:5000"
                           "Access-Control-Allow-Methods" "GET, OPTIONS"
                           "Access-Control-Allow-Headers" "Content-Type"}))
      :else
      {:status 404
       :body   "Not found"})))

(defonce dev-server (atom nil))

(defn dev-start! []
  (reset! dev-server (s/run-server #'dev-handler {:port 5001}))
  (println "Development server listening on http://localhost:5001"))
