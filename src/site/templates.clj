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
     [:div
      {:class "bg-stone-100 text-stone-700 min-h-screen font-serif 
               font-normal text-xl flex flex-col"}

      :children] 
     [:script {:src "./assets/js/websocket.js"}]]]
   children))

(defn default-page [& content]
  (base
   (com/site-menu)

   [:div
    {:class "mt-3 px-3 grow"}
    content]
   
   [:footer
    {:class "pt-4 pb-8 px-4 flex-none"}
    [:span
    {:class "flex flex-row justify-center"} 
     "Have a great day, eh!"]]))

(defn post [content]
  (default-page
   
   [:article
    {:class "pt-4 prose-xl prose-h1:text-3xl prose-h2:text-2xl 
             prose-headings:font-medium"}
    content]

   [:div
    "Thanks for reading!"]
   [:footer
    (str "Copyright © " (.getYear (java.time.LocalDate/now)) " Stef Coetzee")]))
