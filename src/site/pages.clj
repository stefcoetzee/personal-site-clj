(ns site.pages
  (:require
   [clojure.string :as str]
   [site.components :as c]
   [site.templates :as t]))

(defn home []
  (t/base
   {:page-title "Home"}
   [:div
    {:class "flex justify-center items-center h-screen"}
    [:div
     {:class "flex flex-col space-y-2 border border-stone-300 hover:border-stone-400 transition-colors duration-300 w-fit px-6 pt-4 
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
   {:page-title "About"
    :current-page "about"}

   [:div
    {:class "flex flex-col space-y-3"}
    [:p
     "Hi there! I’m Stef."]

    [:p
     "I see industrial progress acceleration towards an abundant future as the 
      central focus of my professional life."]

    [:p
     "Learn more about what I’m doing "
     (c/link "/now" "now")
     ". "
     "For more information, see my "
     (c/link "/resume" "resume")
     "."]

    [:p
     "If, like me, you enjoy discovering books by seeing what others have read,
      take a look at my "
     (c/link "/bookshelf" "bookshelf")
     ". I write infrequently and post on my "
     (c/link "/blog" "blog")
     " even less often."]

    [:p
     "Email: "
     (c/link "mailto:stef@stefcoetzee.com"
             [:span {:class "font-sans text-base"} "stef@stefcoetzee.com"])]

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
     "This site is made with "
     (c/link "https://babashka.org/" "Babashka")
     ", a fast native Clojure scripting runtime. "
     "Site styling is done via "
     (c/link "https://tailwindcss.com/" "Tailwind CSS")
     "."]

    [:div {:class "text-stone-400"}
     (c/last-updated-month "2024-03-08")]]))

(defn now []
  (t/default-page
   {:page-title "Now"
    :current-page "now"}
   [:div
    {:class "flex flex-col space-y-3"}
    [:p
     [:span {:class "flex flex-row items-center justify-start space-x-1.5"}
      [:span
       "Location: Toronto, Canada"]
      [:span
       "🇨🇦"]]]

    [:p
     "I'm part of a multidisciplinary team working on mine-hoist control systems,
      in support of the increased materials supply an "
     (c/link "/about" "abundant future")
     " inevitably implies. "
     "My day-to-day work entails a blend of industrial automation and electrical 
      engineering design, with some exposure functional safety as well."]]

   [:div {:class "my-5"}
    "This is a "
    (c/link "https://nownownow.com" "/now" {:new-tab? true})
    " page."]

   [:div {:class "my-5 text-stone-400"}
    (c/last-updated-month "2024-03-08")]))

(defn blog [posts]
  (t/default-page
   {:page-title "Blog"
    :current-page "blog"}
   [:div
    [:div
     {:class "flex flex-col space-y-4"}
     (for [post posts]
       (let [metadata (:metadata post)]
         [:div
          {:class "flex flex-col space-y-2 px-4 pt-4 pb-8 
                   border border-stone-300 hover:border-stone-400 transition-colors duration-500 ease-in-out transform 
                   hover:-translate-y-1 hover:scale-105"}
          [:div {:class "font-medium"}
           [:a {:href (str "/" (:slug metadata))}
            (:title metadata)]]
          (when (contains? metadata :description)
            [:div
             (:description metadata)])
          [:div {:class "text-stone-400 text-sm font-serif flex items-center"}
           [:svg {:class "w-4 h-4 mr-1.5" :fill "none" :stroke "currentColor" :viewBox "0 0 24 24"}
            [:path {:stroke-linecap "round" :stroke-linejoin "round" :stroke-width "1.5"
                    :d "M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"}]]
           (c/format-date (:published metadata))]]))]]))

(def books
  [{:title "The Promise of Artificial Intelligence: Reckoning and Judgment"
    :authors ["Brian Cantwell Smith"]}
   {:title "Boom: Bubbles and the End of Stagnation"
    :authors ["Byrne Hobart" "Tobias Huber"]}
   {:title "The Well-Grounded Rubyist"
    :meta {:edition "3rd"}
    :authors ["David A. Black"
              "Joseph Leo III"]}
   {:title "Good Profit"
    :authors ["Charles Koch"]}
   {:title "Good Strategy, Bad Strategy"
    :authors ["Richard Rumelt"]}
   {:title "Conspiracy: Peter Thiel, Hulk Hogan, Gawker, and the Anatomy of Intrigue"
    :authors ["Ryan Holiday"]}
   {:title "The Richest Man in Babylon"
    :authors ["George S. Clason"]}
   {:title "Hillbilly Elegy"
    :authors ["J.D. Vance"]}
   {:title "En toe fire hulle my"
    :meta {:translated-title "And then they fired me"}
    :authors ["Jannie Mouton"]}
   {:title "Anton Rupert: ’n Lewensverhaal"
    :authors ["Ebbe Dommisse"]}
   {:title "The Catcher in the Rye"
    :authors ["J.D. Salinger"]}
   {:title "Die Afrikaners"
    :meta {:translated-title "The Afrikaners"}
    :authors ["Hermann Giliomee"]}
   {:title "’n Tydreisigersgids vir Suid-Afrika in 2030"
    :meta {:translated-title "A Time Traveller’s Guide to South Africa in 2030"}
    :authors ["Frans Cronjé"]}
   {:title "The Lessons of History"
    :authors ["Will Durant"
              "Ariel Durant"]}
   {:title "The Seven Habits of Highly Effective People"
    :authors ["Stephen R. Covey"]}
   {:title "Benjamin Franklik: An American Life"
    :authors ["Walter Isaacson"]}])

(defn bookshelf [books]
  (t/default-page
   {:page-title "Bookshelf"
    :current-page "bookshelf"}

   [:blockquote {:class "mb-5 flex flex-col bg-stone-50 px-6 py-4 border-l-4 border-orange-500/30"}
    [:span {:class "italic"}
     "A good book gets better on the second reading. 
        A great book on the third. 
        Any book not worth rereading isn’t worth reading."]
    [:span {:class "text-right"}
     "— Nassim N. Taleb"]]

   [:div {:class "mt-5 flex flex-col space-y-3"}
    [:p
     "Some books I think are work rereading are listed below 
      (in reverse reading order):"]

    [:ul {:class "list-disc list-outside ml-5 marker:text-stone-400
                  flex flex-col space-y-1"}
     (for [book books]
       [:li
        [:em {:class "italic"}
         (:title book)
         (when (:meta book)
           (let [book-meta (:meta book)]
             [:span {:class "not-italic"}
              " ("
              (when (:edition book-meta)
                (str (:edition book-meta) " Ed."))
              (when (:translated-title book-meta)
                [:span
                 "tr. "
                 [:em {:class "italic"}
                  (:translated-title book-meta)]])
              ")"]))]
        [:span
         (str ", by "
              (cond
                (= (count (:authors book)) 2)
                (str (first (:authors book))
                     " and "
                     (second (:authors book)))

                :else (first (:authors book))))]])]]))

(defn resume []
  (t/default-page
   {:page-title "Resume"
    :current-page "resume"}

   [:div {:class "flex flex-col space-y-3"}
    [:h1
     {:class "font-bold text-5xl"}
     "Stef Coetzee"]

    [:div {:class "italic"}
     "Toronto, Canada"]

    [:div
     (c/link "mailto:stef@stefcoetzee.com"
             [:span {:class "font-sans text-base"} "stef@stefcoetzee.com"])]]

   [:div {:class "my-5"}
    "Engineer with industrial automation, electrical engineering,
     and business software experience gained at industry-leading engineering firms.
     Self-starter effective at reaching goals in demanding environments.
     Holds bachelor’s and master’s degrees in electrical engineering. "]

   [:div {:class "my-5 flex flex-col space-y-5"}
    [:h2 {:class "font-bold text-3xl"}
     "Work experience"]

    [:div {:class "flex flex-col space-y-3"}
     [:h3 {:class "font-bold text-xl"}
      "Electrical Designer"]

     [:span
      "January 2024 – present"]

     [:div {:class "italic"}
      [:div
       "Hepburn Engineering Inc"]
      [:div
       "Toronto, Canada"]]

     [:div {:class "flex flex-col space-y-3"}
      [:p
       "Member of the Mining Electrical design team, 
        working on multi-disciplinary mine-hoist projects."]

      [:p
       "Contributions range from control system design to PLC and HMI software development,
        with some exposure to functional safety applied to mine-hoist machinery."]]]

    [:div {:class "flex flex-col space-y-3"}
     [:h3 {:class "font-bold text-xl"}
      "Systems Engineer"]

     [:span
      "May 2023 – January 2024"]

     [:div {:class "italic"}
      [:div
       "Rouxcel Technology (Pty) Ltd"]
      [:div
       "Stellenbosch, South Africa"]]

     [:div {:class "flex flex-col space-y-3"}
      [:p
       "Development, manufacture, and operation of software and hardware systems."]

      [:p
       "Key contributions include software systems design and development for 
        reliable and efficient data processing, and business management."]

      [:p
       "Software tools and technologies used: 
        Python (leveraging packages such as FastAPI, Polars, etc.), MySQL, JavaScript with React, Google Cloud Platform, Git."]]]

    [:div {:class "flex flex-col space-y-3"}
     [:h3 {:class "font-bold text-xl"}
      "Control & Automation Engineer"]

     [:span
      "February 2023 – April 2023"]

     [:div {:class "italic"}
      [:div
       "Technologies Group, Hatch Africa"]
      [:div
       "Johannesburg, South Africa"]]

     [:div
      {:class "flex flex-col space-y-3"}
      [:p
       "Early development of Zimplats Furnace 2 PLC system on the 
        Schneider Control Expert platform."]]]

    [:div {:class "flex flex-col space-y-3"}
     [:h3
      {:class "font-bold text-xl"}
      "Intermediate Control & Automation Engineer"]

     [:span
      "April 2021 – January 2023"]

     [:div {:class "italic"}
      [:div
       "Technologies Group, Hatch Africa"]
      [:div
       "Johannesburg, South Africa"]]

     [:div {:class "flex flex-col space-y-3"}
      [:p
       "Software team member of the furnace control system upgrade project at 
        Polokwane Metallurgical Complex (part of the Anglo American Platinum fleet), 
        using Siemens PCS 7. 
        Includes operation of cooling-water supply pump motors driven by ABB VFDs."]

      [:p
       "Technologies control and automation team member of the Zimplats Furnace 2 project, 
        platinum smelter scope.
        Responsible for control and automation deliverables, 
        including deliverables developed in Siemens COMOS."]

      [:p
       [:span
        "Control and automation team member on the Northam Platinum project, 
         a US$60 million detail design and commissioning project in Limpopo, 
         South Africa. 
         Activities and responsibilities included:"]
       [:ul {:class "list-outside list-disc ml-4 marker:text-stone-400"}
        [:li
         "ABB Freelance distributed control system software development and 
          implementation, 
          including use of Danfoss VFDs as fan motor soft starters;"]
        [:li
         "Smelter furnace instrumentation installation and commissioning on an 
          expedited schedule."]]]]]

    [:div {:class "flex flex-col space-y-3"}
     [:h3 {:class "font-bold text-xl"}
      "Junior Control & Automation Engineer"]

     [:span
      "February 2019 – March 2021"]

     [:div {:class "italic"}
      [:div
       "Project Delivery Group, Hatch Africa"]
      [:div
       "Johannesburg, South Africa"]]

     [:div {:class "flex flex-col space-y-2"}
      [:p
       [:span
        "Control and automation team member for the Iron Bridge project, 
         a US$2.6 billion, 20Mtpa magnetiteprocessing facility in Western Australia. 
         Using T-SQL and VBA:"]
       [:ul {:class "list-outside list-disc ml-4 marker:text-stone-400"}
        [:li
         "Saved hundreds of hours of review and rework by developing diagnostics 
          and progress-tracking tooling for the project control and automation digital twin; and"]
        [:li
         "Developed a bulk equipment-tag generator to automatically produce 
          electrical and instrumentation termination tags, 
          removing the need for error-prone manual entry by designers and engineers."]]]]]]

   [:div {:class "my-5 flex flex-col space-y-5"}
    [:h2 {:class "font-bold text-3xl"}
     "Education"]

    [:div {:class "flex flex-col space-y-3"}
     [:h3 {:class "font-bold text-xl"}
      "Master of Electrical Engineering"]

     [:span
      "February 2017 – December 2018"]

     [:div
      {:class "italic"}
      [:div
       "Stellenbosch University"]
      [:div
       "South Africa, South Africa"]]

     [:div {:class "flex flex-col space-y-3"}
      [:p
       "Awarded an Innovation Scholarship by South Africa’s National Research 
        Foundation for research into the use of model predictive control to reduce a 
        given residence’s dependency on the national electrical grid."]

      [:p
       [:span
        "Thesis permalink "]
       (c/link "http://hdl.handle.net/10019.1/105979"
               [:span {:class "font-sans text-base"}
                "http://hdl.handle.net/10019.1/105979"])]]]

    [:div {:class "flex flex-col space-y-3"}
     [:h3 {:class "font-bold text-xl"}
      "Bachelor of Electrical and Electronic Engineering"]

     [:span
      "2013 – 2016"]

     [:div {:class "italic"}
      [:div
       "Stellenbosch University"]
      [:div
       "South Africa, South Africa"]]

     [:div
      [:p
       "GPA: 3.3 / 4.0"]]]]

   [:div {:class "my-5 text-stone-400"}
    (c/last-updated-month "2024-12-04")]))
