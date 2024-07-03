(ns site.pages
  (:require [clojure.string :as str]
            [site.templates :as t]))

(defn home []
  (t/base
   [:div
    {:class "flex justify-center items-center h-screen"}
    [:div
     {:class "flex flex-col space-y-2 border border-gray-900 w-fit px-6 pt-4 
              pb-6 font-serif"}
     [:span
      {:class "text-stone-900"}
      [:a
       {:class "font-medium"
        :href "/"}
       "Stef Coetzee"]]
     [:nav
      (into
       [:ul
        {:class "flex flex-col"}]
       (let [menu-items ["About" "Now" "Blog" "Bookshelf" "Resume"]]
         (for [menu-item menu-items]
           [:li
            {:class "w-fit"}
            [:a
             {:href (str "/" (str/lower-case menu-item))}
             menu-item]])))]]]))

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

(defn about []
  (t/default-page
      
   [:div
    [:p 
     "Hi, I’m Stef."]
    
    [:p 
     "I see accelerating industrial progress as the central the central
      focus of my professional life."]
    
    [:p 
     "Learn more about what I’m working on "
     [:a
      {:class "italic"
       :href "/now"}
      "Now"]
     "."]]))

(comment
  (about)
  :rcf)

(defn now []
  (t/default-page 
   [:div 
    "I work in industrial automation and electrical engineering 
     at an manufacturer of mining and replenishment-at-sea systems."]))

(defn bookshelf []
  (t/default-page
   [:blockquote
    "A good book gets better on the second reading. A great book on the third. Any book not worth rereading isn’t worth reading."]
   [:p "Hey, Ma!"]))


(defn resume []
  (t/default-page
   [:h1
    "Stef Coetzee"]

   [:div
    "Toronto, Canada"]

   [:div
    [:div
     [:a
      {:href "mailto:stef@stefcotzee.com"}
      "stef@stefcoetzee.com"]]]))

(comment
  (resume)
  :rcf)
