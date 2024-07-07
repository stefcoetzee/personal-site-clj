(ns site.pages
  (:require [clojure.string :as str]
            [site.components :as c]
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
            (c/link (str "/" (str/lower-case menu-item))
                    menu-item)])))]]]))

(defn about []
  (t/default-page
   [:div
    {:class "flex flex-col space-y-3"}
    [:p 
     "Hi, I’m Stef."]
    
    [:p 
     "I see industrial progress acceleration as the central focus of my 
      professional life."]
    
    [:p
     "Email: "
     (c/link "mailto:stef@stefcoetzee.com"
             [:span {:class "font-sans text-base"} "stef@stefcoetzee.com"])]
    
    [:p
     "I grew up in South Africa and studied electrical and electronic 
      engineering at Stellenbosch University."]
    
    [:p
     "Learn more about what I’m doing "
     (c/link "/now" "now")
     "."]
    
    [:p
     [:span
      "Elsewhere:"
      [:ul
       {:class "list-disc list-outside ml-5 marker:text-stone-400"}
       [:li
        (c/link "https://github.com/stefcoetzee"
                [:span
                 {:class "font-sans text-base"}
                 "stefcoetzee"])
        " on GitHub;"]
       [:li
        (c/link "https://x.com/stef_coetzee"
                [:span
                 {:class "font-sans text-base"}
                 "stef_coetzee"])
        " on X;"]
       [:li
        (c/link "https://www.linkedin.com/in/stefcoetzee/"
                "LinkedIn")
        "."]]]]
    
    [:p
     "If, like me, you enjoy discovering books by seeing what others have read, 
      take a look at my "
     (c/link "/bookshelf" "bookshelf") 
     ". I write infrequently and post on my "
      (c/link "/blog" "blog")
      " even less often."]]
   
   ))

(defn now []
  (t/default-page 
   [:div
    {:class "flex flex-col space-y-2"}
    [:p
     "Location: Toronto, Canada "]

    [:p
     "I’m employed by a manufacturer of mining—most notably mine hoists—and 
      replenishment-at-sea systems.
      The scope of my work spans the fields of industrial automation and electrical
      engineering."]]))

(defn blog [posts]
  (t/default-page
   [:div
    [:div
     {:class "flex flex-col space-y-4 mt-4"}
     (for [post posts]
       (let [metadata (:metadata post)]
         [:div
          {:class "flex flex-col space-y-0"}
          [:div
           {:class "font-medium"}
           [:a
            {:class "font-medium"
             :href (str "/" (:slug metadata))}
            (:title metadata)]]
          [:div
           {:class "text-stone-400 font-mono font-light text-sm"}
           (:published metadata)]
          (when (contains? metadata :description)
            [:div
             {:class ""}
             (:description metadata)])]))]]))

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
