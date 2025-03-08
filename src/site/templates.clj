(ns site.templates
  (:require [site.components :as com]))

(defn base [& args]
  (let [[{:keys [page-title]} & children] (if (map? (first args))
                                            args
                                            (cons {} args))]
    (com/insert-children
     [:html
      {:lang "en"}
      [:head
       [:meta {:charset "utf-8"}]
       [:meta {:name    "viewport"
               :content "width=device-width, initial-scale=1"}]
       [:link {:rel  "stylesheet"
               :href "/assets/css/fonts.css"}]
       [:link {:rel  "stylesheet"
               :href "/assets/css/main.css"}]
       [:link {:rel  "stylesheet"
               :href "/assets/css/typography.css"}]
       [:link {:rel  "stylesheet"
               :href "/assets/css/prism.css"}]
       [:title (if page-title
                 (str page-title " · Stef Coetzee")
                 "Stef Coetzee")]]
      [:body
       [:div
        {:class "bg-stone-100 text-stone-700 min-h-screen font-serif 
               font-normal text-xl flex flex-col"}

        :children]
       (when (= (System/getProperty "BB_ENV") "development")
         [:script {:src "/assets/js/websocket.js"}]) ; TODO: include when not building for deployment ("production")
       [:script {:src "/assets/js/prism.js"}]]]
     children)))

(defn default-page [& args]
  (let [[opts & content] (if (map? (first args))
                           args
                           (cons {} args))]
    (base
     opts

     [:div
      {:class "grow flex flex-col lg:flex-row lg:justify-between mt-4 px-4 sm:px-8 md:px-12 lg:px-16 xl:px-24"}
      [:div
       {:class "block lg:hidden"}
       (com/site-menu (:current-page opts))]

      [:div
       {:class "lg:mx-auto lg:px-0 lg:max-w-2xl lg:mt-10 grow mt-4"}
       content]

      [:div
       {:class "hidden lg:flex lg:flex-col lg:items-end lg:w-64"}
       [:div
        {:class "w-fit px-6 pt-4 pb-6 font-serif sticky top-10"}
        (com/site-menu (:current-page opts))]]]

     [:footer
      {:class "pt-4 pb-8 px-4 flex-none"}
      [:span
       {:class "flex flex-row justify-center"}
       [:span
        [:span {:class "italic"}
         "Accelerate industrial progress "]
        [:span "🚀"]]]])))

(defn post [content]
  (default-page

   [:article
    {:class "pt-4 custom-prose"
     #_"pt-4 prose-xl prose-h1:text-3xl prose-h2:text-2xl 
             prose-headings:font-medium prose-ol:list-decimal"}
    content]

   [:div {:class "my-6"}
    "Thanks for reading!"]
   [:footer
    (str "© " (.getYear (java.time.LocalDate/now))
         " Stef Coetzee. All rights reserved.")]))
