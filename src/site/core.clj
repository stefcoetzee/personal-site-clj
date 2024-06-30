(ns site.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [babashka.fs :as fs]
            [hiccup2.core :as h]
            [markdown.core :as md]
            [site.templates :as t]
            [site.pages :as pages]))

(require '[babashka.pods :as pods])
(pods/load-pod 'retrogradeorbit/bootleg "0.1.9")
(require '[pod.retrogradeorbit.bootleg.utils :as bl])

(def pub-dir "public")

(def posts-dir "content/posts")

(def assets-dir "content/assets")

(defn parse-post 
  "Return post data, parsed from file at `post-path`."
  [post-path]
  (let [post-file            (fs/file post-path)
        [_ year month day _] (re-matches
                              #"([0-9]{4})-([0-9]{2})-([0-9]{2})-(.*)\.md"
                              (fs/file-name post-file))]
    (with-open [rdr (io/reader post-file)]
      (let [raw-body                (slurp rdr)
            {:keys [metadata html]} (md/md-to-html-string-with-meta
                                     raw-body
                                     :heading-anchors true
                                     :footnotes? true)]
        {:metadata  (assoc (into {} (for [[k v] metadata]
                                      [k (first v)]))
                           :published (str/join "-" [year month day]))
         :html-body html}))))

(defn parse-posts 
  "Return post data, parsed from files in `dir`."
  [dir]
  (->> (fs/list-dir dir)
       (sort)
       (map parse-post)
       (reverse)
       (into [])))

(comment 
  (map :metadata (parse-posts posts-dir))

  (bl/hiccup-seq->html
   (bl/html->hiccup-seq (:html-body (first (parse-posts "content/posts")))))
  :rcf)

;; Render

(defn render-page [page-data]
  (let [out-file (fs/file (fs/path (str pub-dir "/" (:slug page-data) ".html")))]
    (spit out-file
          (h/html {:mode :html
                   :escape-strings? false}
                  "<!DOCTYPE html>"
                  (:content page-data)))))

(defn render-pages []
  (run! render-page
        [{:slug    "index"
          :content (pages/home)}
         {:slug    "blog"
          :content (pages/blog (parse-posts posts-dir))}
         {:slug    "now"
          :content (pages/now)}]))

(defn render-post
  "Render post from provided `post-data`."
  [post-data]
  (let
   [out-file
    (fs/file (fs/path (str pub-dir "/" (:slug (:metadata post-data)) ".html")))]
    (io/make-parents out-file)
    (spit out-file (h/html {:mode :html
                            :escape-strings? false}
                           "<!DOCTYPE html>"
                           (t/post (:html-body post-data))))))

(defn render-posts 
  "Render posts from provided `posts-data`."
  [posts-data]
  (run! (fn [post-data] (render-post post-data)) posts-data))

;; Static assets

(defn copy-assets 
  "Copy assets in `src` directory to `dest` directory."
  [src dest]
  (fs/copy-tree src
                dest
                {:replace-existing true}))

;; Showtime!

(defn clean
  "Remove `dir`."
  [dir]
  (fs/delete-tree dir))

(defn build [& _args]
  (clean pub-dir)
  (copy-assets (str assets-dir "/js")
               (str pub-dir "/assets/js"))
  (copy-assets (str assets-dir "/css")
               (str pub-dir "/assets/css"))
  (render-pages)
  (render-posts (parse-posts posts-dir)))
