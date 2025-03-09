(ns site.templates
  (:require [hiccup2.core :as h]
            [cheshire.core :as json]
            [site.components :as com]))

(defn base [& args]
  (let [[{:keys [page-title]} & children] (if (map? (first args)) args (cons {} args))]
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
       [:link {:rel "icon"
               :type "image/png"
               :href "/favicon.png"}]
       [:title (if page-title
                 (str page-title " · Stef Coetzee")
                 "Stef Coetzee")]]
      [:body
       [:div
        {:class "bg-stone-100 text-stone-700 min-h-screen font-serif 
               font-normal text-xl flex flex-col"}
        :children]
       (when (= (System/getProperty "BB_ENV") "development")
         [:script
          {:type "importmap"}
          (h/raw
           (json/generate-string
            {:imports {"squint-cljs/core.js" "https://cdn.jsdelivr.net/npm/squint-cljs@0.4.81/src/squint/core.js"}}))])
       (when (= (System/getProperty "BB_ENV") "development")
         [:script {:type "module"
                   :src  "/js/hot-reload.js"}])
       [:script {:src "/assets/js/prism.js"}]]]
     children)))

(defn default-page [& args]
  (let [[opts & content] (if (map? (first args)) args (cons {} args))]
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

(defn post [& args]
  (let [[opts & content] (if (map? (first args)) args (cons {} args))]
    (default-page
     (assoc opts :page-title (:title opts))

     [:h1
      {:class "font-medium text-3xl mt-4 mb-6"}
      (:title opts)]

     [:span
      {:class "text-stone-400 text-md mt-6"}
      (if (:last-updated opts)
        "Updated: "
        "Published: ")
      (if (:last-updated opts)
        (com/format-date (-> (:last-updated opts)
                             .toInstant
                             (.atZone (java.time.ZoneId/systemDefault))
                             (.format (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd"))))
        (com/format-date (:published opts)))]

     [:article
      {:class "my-6 prose"}
      content]

     [:span
      {:class "text-stone-400 text-md my-6"}
      "Published: "
      (com/format-date (:published opts))]

     [:div {:class "my-6"}
      "Thanks for reading!"]
     [:footer
      {:class "flex flex-row justify-center"}
      [:span
       (str "© " (.getYear (java.time.LocalDate/now))
            " Stef Coetzee. All rights reserved.")]])))
