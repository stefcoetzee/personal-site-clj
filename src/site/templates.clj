(ns site.templates
  (:require [site.components :as com]))

(defn base [& children]
  (com/insert-children
   [:html
    {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name    "viewport"
             :content "width=device-width, initial-scale=1"}]
     [:link {:rel "stylesheet"
             :href "./assets/css/main.css"}]
     [:link {:rel "stylesheet"
             :href "./assets/css/fonts.css"}]
     [:title "Stef Coetzee"]]
    [:body
     {:class "bg-stone-100 text-stone-700 font-serif font-normal text-2xl"}

     :children

     [:script {:src "./assets/js/websocket.js"}]]]
   children))

(defn default-page [& content]
  (base
   [:div
    [:div
     [:span
      "Stef Coetzee"]
     (com/nav)]
    
    (when content
      content)]))

(defn post [content]
  (base 
   [:div
    [:div
     "The post shall begin shortly."]

    content
    
    [:div
     "Thanks for reading!"]
    
    [:footer
     (str "Copyright © " (.getYear (java.time.LocalDate/now)) " Stef Coetzee")]]))
