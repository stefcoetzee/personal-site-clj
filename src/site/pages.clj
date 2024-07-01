(ns site.pages
  (:require [site.components :as com]
            [site.templates :as t]))

(defn home []
  (t/base
   [:div
    {:class "flex justify-center items-center h-screen"}
    [:div
     {:class "flex flex-col space-y-2 border border-gray-900 w-fit px-6 pt-4 pb-6 font-serif"}
     [:span.text-stone-900
      [:a
       {:href "/"}
       "Stef Coetzee"]]
     (com/nav)]]))

(defn blog [posts]
  (t/default-page
   [:div
    [:h1 "Posts"]
    (for [post posts]
      (let [metadata (:metadata post)]
        [:p
         [:div
          [:span
           {:class "block text-lg font-mono text-stone-500"}
           (:published metadata)]
          [:a {:href (str "/" (:slug metadata))}
           (:title metadata)]]
         (when (contains? metadata :description)
          [:div
           (:description metadata)])]))]))

(defn now []
  (t/base 
   [:div 
    "I work in industrial automation and electrical engineering 
     at an manufacturer of mining and replenishment-at-sea systems."]))
