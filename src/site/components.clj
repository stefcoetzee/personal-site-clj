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
    (if (> (count idx-route) 1)
      (assoc-in component
                (butlast idx-route)
                (vec (concat local-before
                             (if (seq? children) (into [] children) [children])
                             local-after)))
      (vec (concat local-before
                   (if (seq? children) (into [] children) [children])
                   local-after)))))

(defn link [href children & [{:keys [ext-class]}]]
  (insert-children
   [:a
    {:class (str "hover:text-orange-600 transition duration-200 ease-in-out 
                  underline decoration-2 decoration-orange-500/30 
                  underline-offset-4 "
                 ext-class)
     :href href}
    :children]
   children))

(defn site-menu [current-page]
  [:div
   {:class "flex flex-col space-y-1 border border-gray-900 px-4 pt-2 pb-4 m-4"}
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
          (link (str "/" (str/lower-case menu-item))
                menu-item
                (when (= (str/lower-case menu-item) current-page)
                  {:ext-class "text-orange-600"}))])))]])

(defn format-date
  "Format date string `date-str` to \"Day Month Year\"."
  [date-str]
  (let [date      (java.time.LocalDate/parse date-str)
        formatter (java.time.format.DateTimeFormatter/ofPattern "MMMM")
        month     (.format date formatter)]
    (str (.getDayOfMonth date) " " month " " (.getYear date))))

(defn last-updated [date-str]
  (let [date      (java.time.LocalDate/parse date-str)
        formatter (java.time.format.DateTimeFormatter/ofPattern "MMMM")
        month     (.format date formatter)]
    (str "Updated: "
         (.getDayOfMonth date) " "
         month " "
         (.getYear date))))

(defn last-updated-month
  "Last-updated date formatted up to the month.
   
   E.g. \"Updated: December 2024\" for \"2024-12-04\"."
  [date-str]
  (let [date      (java.time.LocalDate/parse date-str)
        formatter (java.time.format.DateTimeFormatter/ofPattern "MMMM")
        month     (.format date formatter)]
    (str "Updated: "
         month " "
         (.getYear date))))

(comment
  (last-updated-month "2024-12-04")
  (last-updated "2024-12-04")
  :rcf)
