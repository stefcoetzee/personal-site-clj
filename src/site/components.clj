(ns site.components
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
    [:ul
     {:class "flex flex-row"}]
    (let [menu-items ["About" "Now" "Blog" "Bookshelf" "Resume"]]
      (for [menu-item menu-items]
        [:li
         {:class "w-fit"}
         [:a
          {:href (str "/" (str/lower-case menu-item))}
          menu-item]])))])

(defn site-menu []
  [:div
   {:class "flex flex-col space-y-1 border border-gray-900 px-4 pt-2 pb-4 m-2"}
   [:div 
    [:a
     {:class "text-xl font-medium"
      :href  "/"}
     "Stef Coetzee"]]
   [:nav
    (into
     [:ul
      {:class "grid grid-cols-3 gap-1 text-xl"}]
     (let [menu-items ["About" "Now" "Blog" "Bookshelf" "Resume"]]
       (for [menu-item menu-items]
         [:li
          {:class "w-fit"}
          [:a
           {:href (str "/" (str/lower-case menu-item))}
           menu-item]])))]])

(comment
  (site-menu)
  :rcf)
