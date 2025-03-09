(ns dev.client.hot-reload)

(def timestamp (atom 0))

(defn fetch-timestamp []
  (-> (js/fetch (str "http://localhost:5001/hot-reload?last-modified=" @timestamp))
      (.then #(.text %))))

(defn set-initial-timestamp [text]
  (js/console.log "Initial timestamp " text)
  (reset! timestamp text))

(defn reload [new-timestamp]
  (when (not= @timestamp new-timestamp)
    (js/console.log "Reload " new-timestamp)
    (reset! timestamp new-timestamp) ; prevent infinite render-reload loop
    (js/location.reload)))

(defn polling-loop []
  (-> (fetch-timestamp)
      (.then reload)
      (.finally #(js/setTimeout polling-loop 600))))

(js/console.log "Hot reload running")
(-> (fetch-timestamp)
    (.then set-initial-timestamp)
    (.then polling-loop))
