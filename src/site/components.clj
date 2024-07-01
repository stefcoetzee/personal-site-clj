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
    [:ul]
    (let [menu-items ["About" "Now" "Blog" "Bookshelf" "Resume"]]
      (for [menu-item menu-items]
        [:li
         [:a
          {:href (str "/" (str/lower-case menu-item))}
          menu-item]])))])
