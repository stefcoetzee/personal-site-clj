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
    #_[:h1
       {:class "font-medium text-2xl"}
       "Posts"]
    [:div
     {:class "flex flex-col space-y-4 mt-4"}
     (for [post posts]
       (let [metadata (:metadata post)]
         [:div
          {:class "flex flex-col space-y-0"}
          [:div
           {:class "font-medium"}
           [:a
            {:href (str "/" (:slug metadata))}
            (:title metadata)]]
          [:div
           {:class "text-stone-400 font-sans text-base"}
           (:published metadata)]
          (when (contains? metadata :description)
            [:div
             (:description metadata)])]))]]))

(defn about []
  (t/default-page
      
   [:div
    {:class "flex flex-col space-y-2"}
    [:p 
     "Hi, I’m Stef."]
    
    [:p 
     "I see industrial progress acceleration as the central focus of my 
      professional life."]
    
    [:p
     "Email: "
     [:a
      {:class "font-sans text-base"}
      "stef@stefcoetzee.com"]]
    
    [:p
     "I grew up in South Africa and studied electrical and electronic 
      engineering at Stellenbosch University."]
    
    [:p 
     "Learn more about what I’m doing "
     [:a
      {:class "italic"
       :href "/now"}
      "now"]
     "."]
    
    [:p
     {:class "mt-2"}
     [:p
      "Elsewhere:"
      [:ul
       {:class "list-disc list-outside ml-5"}
       [:li
        [:a
         {:class "font-sans text-base"
          :href "https://github.com/stefcoetzee"}
         "stefcoetzee"]
        " on GitHub;"]
       [:li
        [:a
         {:class "font-sans text-base"
          :href "https://x.com/stef_coetzee"}
         "stef_coetzee"]
        " on X."]]]]
    
    [:p
     "If, like me, you enjoy discovering books by seeing what others have read, 
      take a look at my "
     [:a
      {:class "italic"
       :href "/bookshelf"}
      "bookshelf"]
     ". I write infrequently and post on my "
      [:a
       {:class "italic"
        :href "/blog"}
       "blog"]
      " even less often."]]
   
   ))

(comment
  (about)
  :rcf)

(defn now []
  (t/default-page 
   [:div 
    "I work in the fields of industrial automation and electrical engineering 
     at an manufacturer of mining—most notably mine hoists—and 
     replenishment-at-sea systems."]))

(defn bookshelf []
  (t/default-page
   [:blockquote
    "A good book gets better on the second reading. 
     A great book on the third. 
     Any book not worth rereading isn’t worth reading."]))


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
