(ns site.pages
  (:require [site.templates :as t]))

(defn home []
  (t/base
   [:div
    [:span "Stef Coetzee"]
    (t/nav)]))

(defn blog [posts]
  (t/base
   [:div
    [:h1 "Posts"]
    (for [post posts]
      (let [metadata (:metadata post)]
        [:p
         [:div
          [:span (str "[" (:published metadata) "] ")]
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

(comment
  (into [:div]
        '([:p "foo"]
          [:p "bar"]))
  :rcf)
