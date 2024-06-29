(ns site.core
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [babashka.fs :as fs]
            [hiccup2.core :as h]
            [markdown.core :as md]
            [site.templates :as t]))

(def pub-dir "public")

(def posts-dir "content/posts")

(defn -main [& _args]
  (prn (str "This thing on?"))
  (let [file     (first (fs/list-dir "content/posts"))
        out-file (fs/file (fs/path "public/index.html"))]
    (with-open [rdr (io/reader (fs/file file))]
      (spit out-file
            (h/html {:mode            :html
                     :escape-strings? false}
                    "<!DOCTYPE html>"
                    (t/base (md/md-to-html-string (slurp rdr))))))))

;; Render posts

(defn render-post [post-data]
  (let [out-file (fs/file (fs/path (str pub-dir "/"
                                        (:slug (:metadata post-data))
                                        ".html")))]
    (spit
     out-file
     (h/html {:mode :html
              :escape-strings? false}
             "<!DOCTYPE html>"
             (t/base (:html-body post-data))))))

(defn render-posts [posts-data]
  (for [post-data posts-data]
    (render-post post-data)))

(comment
  (render-posts (parse-posts posts-dir))

  (let [posts-data   (parse-posts posts-dir)
        sample-post (first posts-data)
        out-file    (fs/file (fs/path (str pub-dir "/"
                                           (:slug (:metadata sample-post))
                                           ".html")))]
    (spit
     out-file
     (h/html {:mode :html
              :escape-strings? false}
             "<!DOCTYPE html>"
             (t/base (:html-body sample-post)))))
  :rcf)


(defn parse-post [post]
  (let [post-file            (fs/file post)
        [_ year month day _] (re-matches
                              #"([0-9]{4})-([0-9]{2})-([0-9]{2})-(.*)\.md"
                              (fs/file-name post-file))]
    (with-open [rdr (io/reader post-file)]
      (let [raw-body                (slurp rdr)
            {:keys [metadata html]} (md/md-to-html-string-with-meta raw-body)]
        {:metadata  (assoc (into {} (for [[k v] metadata]
                                      [k (first v)])) 
                           :published (str/join "-" [year month day]))
         :html-body html}))))

(defn parse-posts [dir-str]
  (->> (fs/list-dir dir-str)
       (sort)
       (map parse-post)
       (reverse)
       (into [])))

(comment
  (parse-posts "content/posts")

  ;; Parse posts 
  (->> (fs/list-dir "content/posts")
       (sort)
       (map parse-post)
       (reverse))  

  ;; Parse post
  (let [post                 (fs/file (first (fs/list-dir "content/posts")))
        [_ year month day _] (re-matches #"([0-9]{4})-([0-9]{2})-([0-9]{2})-(.*)\.md"
                                         (fs/file-name post))]
    (with-open [rdr (io/reader post)]
      (let [raw-body                (slurp rdr)
            {:keys [metadata html]} (md/md-to-html-string-with-meta raw-body)]
        {:metadata (assoc metadata :published (str/join "-" [year month day]))
         :html-body html})))

  (fs/file (first (fs/list-dir "content/posts")))

  (first (fs/list-dir "content/posts"))

  (re-matches #"([0-9]{4})-([0-9]{2})-([0-9]{2})-(.*)\.md"
              "2024-06-26-hello-world.md")
  (fs/file-name (first (fs/list-dir "content/posts")))
  :rcf)
