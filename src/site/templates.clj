(ns site.templates)

(defn base [body]
  [:html
   {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name    "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:title "Site Title"]]
   [:body

    [:header
     "Something"]

    body

    [:div
     "Something else"]

    [:footer
     (str "Copyright © " (.getYear (java.time.LocalDate/now)) " Stef Coetzee")]

    [:script {:src "assets/js/websocket.js"}]]])
