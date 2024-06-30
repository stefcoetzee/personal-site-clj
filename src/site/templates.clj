(ns site.templates
  (:require [clojure.string :as str]
            [com.rpl.specter :as s]))

(defn index-route
  "Return the index route of value `v` in data structure `ds`."
  [v ds]
  (let [walker (s/recursive-path
                [] p (s/if-path sequential?
                                [s/INDEXED-VALS
                                 (s/if-path [s/LAST (s/pred= v)]
                                            s/FIRST
                                            [(s/collect-one s/FIRST) s/LAST p])]))
        ret    (s/select-first walker ds)]
    (if (or (vector? ret) (nil? ret))
      ret
      [ret])))

(defn insert-children [component children]
  (let [idx-route    (index-route :children component)
        parent-vec   (get-in component (butlast idx-route))
        local-before (subvec parent-vec 0 (last idx-route))
        local-after  (subvec parent-vec (+ 1 (last idx-route)))]
    (assoc-in component
              (butlast idx-route)
              (vec (concat local-before
                           (if (seq? children) (into [] children) [children])
                           local-after)))))

(defn nav []
  [:nav
   (into
    [:ul]
    (let [menu-items ["Home" "About" "Now" "Blog" "Bookshelf" "Resume"]]
      (for [menu-item menu-items]
        [:li
         [:a
          {:href (str "/" (cond (= menu-item "Home") ""
                                :else (str/lower-case menu-item)))}
          menu-item]])))])

(defn base [& children]
  (insert-children
   [:html
    {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name    "viewport"
             :content "width=device-width, initial-scale=1"}]
     [:link {:rel "stylesheet"
             :href "./assets/css/main.css"}]
     [:title "Stef Coetzee"]]
    [:body

     :children

     #_[:footer
        (str "Copyright © " (.getYear (java.time.LocalDate/now)) " Stef Coetzee")]

     [:script {:src "./assets/js/websocket.js"}]]]
   children))

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
