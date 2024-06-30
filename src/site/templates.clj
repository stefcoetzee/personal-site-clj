(ns site.templates
  (:require [clojure.string :as str]))

(defn nav []
  [:nav
   (into [:ul]
         (let [menu-items ["Home"
                           "About"
                           "Now"
                           "Blog"
                           "Bookshelf"
                           "Resume"]]
           (for [menu-item menu-items]
             [:li
              [:a {:href (str "/" (cond
                                    (= menu-item "Home") ""
                                    :else (str/lower-case menu-item)))}
               menu-item]])))])

(defn base [& content]
  [:html
   {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name    "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:title "Stef Coetzee"]]
   [:body

    (when content
      content)

    #_[:footer
       (str "Copyright © " (.getYear (java.time.LocalDate/now)) " Stef Coetzee")]

    [:script {:src "assets/js/websocket.js"}]]])

(defn default-page [& content]
  (base
   [:div
    [:div
     [:span
      "Stef Coetzee"]
     (nav)]
    
    (when content
      content)]))

(defn post [content]
  (base 
   [:div
    [:div
     "The post shall begin shortly."]

    content
    
    [:div
     "Thanks for reading!"]]))
