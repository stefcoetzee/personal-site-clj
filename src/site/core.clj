(ns site.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [babashka.fs :as fs]
            [hiccup2.core :as h]
            [squint.compiler :as squint]
            [markdown.core :as md]
            [site.templates :as t]
            [site.pages :as pages]))

(require '[babashka.pods :as pods])
(pods/load-pod 'retrogradeorbit/bootleg "0.1.9")
(require '[pod.retrogradeorbit.bootleg.utils :as bl])

(def pub-dir "public")

(def posts-dir "content/posts")

(def assets-dir "content/assets")

(defn anchorize-headers [html-str]
  (let [hiccup-seq       (bl/html->hiccup-seq html-str)
        header-anchor-fn (fn [hiccup-el]
                           (if (re-find #"^:h"
                                        (str (first hiccup-el)))
                             (let [el-meta (second hiccup-el)
                                   el-link (str "#" (:id el-meta))]
                               [(first hiccup-el) el-meta
                                [:span {:class "group"}
                                 [:a {:href el-link}
                                  (drop 2 hiccup-el)]
                                 " "
                                 [:a {:class "hidden group-hover:inline 
                                           text-stone-400"
                                      :href  el-link}
                                  "§"]]])
                             hiccup-el))]
    (map header-anchor-fn
         hiccup-seq)))

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
                                     :footnotes? true
                                     :code-style #(str "class=\"language-" % "\""))]
        {:metadata  (assoc (into {} (for [[k v] metadata]
                                      [k (if (coll? v) (vec v) v)]))
                           :published (str/join "-" [year month day]))
         :html-body (anchorize-headers html)}))))

(comment
  (:metadata (parse-post (last (fs/list-dir posts-dir))))
  :rcf)

(defn get-prev-next-post-slugs [slugs]
  (let [slug-count    (count slugs)
        indexed-slugs (->> slugs
                           (map-indexed vector)
                           (into []))]
    (->> (for [[idx _] indexed-slugs]
           (let [next-post-slug (case idx
                                  0 nil
                                  (get slugs (dec idx)))
                 previous-post-slug     (case idx
                                          slug-count nil
                                          (get slugs (inc idx)))]
             {:previous-post-slug previous-post-slug
              :next-post-slug     next-post-slug}))
         (into []))))

(defn add-prev-next-post-slugs [posts-data]
  (let [slugs                (mapv #(-> % :metadata :slug) posts-data)
        prev-next-post-slugs (get-prev-next-post-slugs slugs)]
    (->> (for [[idx pnp-slugs] (map-indexed vector prev-next-post-slugs)]
           (let [post-data (get posts-data idx)]
             (assoc post-data :metadata (merge (:metadata post-data)
                                               pnp-slugs))))
         (into []))))

(comment
  (first (add-prev-next-post-slugs (parse-posts posts-dir)))
  :rcf)

(defn parse-posts
  "Return post data, parsed from files in `dir`."
  [dir]
  (->> (fs/list-dir dir)
       (sort)
       (map parse-post)
       (reverse)
       (into [])
       (add-prev-next-post-slugs)))

(comment
  (mapv :metadata (parse-posts posts-dir))
  :rcf)

;; Compile Squint

(defn prep-dirs! []
  (fs/create-dirs "public/assets/js"))

(defn compile-squint! []
  (println "Compiling Squint CLJS files")
  (prep-dirs!)
  (try
    (spit "public/assets/js/hot-reload.js"
          (squint/compile-string (slurp "src/dev/client/hot_reload.cljs")))
    (println "Squint compilation successful")
    (catch Exception e
      (println "Error encountered attempting to compile Squint: "
               (.getMessage e)))))

;; Render

(defn render-page! [page-data]
  (let [out-file (fs/file (fs/path (str pub-dir "/" (:slug page-data) ".html")))]
    (spit out-file
          (h/html {:mode :html
                   :escape-strings? false}
                  "<!DOCTYPE html>"
                  (:content page-data)))))

(defn render-pages! []
  (run! render-page!
        [{:slug    "index"
          :content (pages/home)}
         {:slug    "about"
          :content (pages/about)}
         {:slug    "now"
          :content (pages/now)}
         {:slug    "blog"
          :content (pages/blog (parse-posts posts-dir))}
         {:slug    "bookshelf"
          :content (pages/bookshelf pages/books)}
         {:slug    "resume"
          :content (pages/resume)}]))

(comment
  (mapv :metadata (parse-posts posts-dir))
  :rcf)

(defn render-post!
  "Render post from provided `post-data`."
  [post-data]
  (let
   [out-file
    (fs/file (fs/path (str pub-dir "/" (:slug (:metadata post-data)) ".html")))]
    (io/make-parents out-file)
    (spit out-file (h/html {:mode :html
                            :escape-strings? false}
                           "<!DOCTYPE html>"
                           (t/post (:metadata post-data) (:html-body post-data))))))

(defn render-posts!
  "Render posts from provided `posts-data`."
  [posts-data]
  (run! (fn [post-data] (render-post! post-data)) posts-data))

;; Static assets

(defn copy-assets!
  "Copy assets in `src` directory to `dest` directory."
  [src dest]
  (fs/copy-tree src
                dest
                {:replace-existing true}))

;; Showtime!

(defn clean!
  "Remove `dir`."
  [dir]
  (fs/delete-tree dir))

(defn generate-redirect!
  "Generate a redirect HTML file with meta refresh tag"
  [from-path to-slug]
  (let [redirect-file (fs/file (fs/path (str pub-dir from-path ".html")))]
    (io/make-parents redirect-file)
    (spit redirect-file
          (h/html {:mode            :html
                   :escape-strings? false}
                  "<!DOCTYPE html>"
                  [:html
                   [:head
                    [:meta {:http-equiv "refresh"
                            :content    (str "0;url=/" to-slug)}]
                    [:title (str "Redirect to " to-slug)]
                    [:link {:rel  "canonical"
                            :href (str "/" to-slug)}]]
                   [:body
                    [:p "Redirecting to "
                     [:a {:href (str "/" to-slug)}
                      to-slug]]]]))))

(defn generate-redirects!
  "Generate redirect pages for all posts that have redirect-from metadata"
  [posts-data]
  (doseq
   [post-data posts-data]
    (when-let [redirects (seq (:redirect-from (:metadata post-data)))]
      (let [target-slug (:slug (:metadata post-data))]
        (doseq [redirect-path redirects]
          (generate-redirect! redirect-path target-slug))))))

(defn build! [& _args]
  (clean! pub-dir)
  (copy-assets! (str assets-dir "/js")
                (str pub-dir "/assets/js"))
  (copy-assets! (str assets-dir "/css")
                (str pub-dir "/assets/css"))
  (copy-assets! (str assets-dir "/root")
                (str pub-dir))
  (when (= (System/getProperty "BB_ENV") "development")
    (compile-squint!))
  (render-pages!)
  (let [posts-data (parse-posts posts-dir)]
    (render-posts! posts-data)
    (generate-redirects! posts-data)))
