(ns leiningen.highlight
  (:require [clojure.main :as m]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.java.browse :as browse]
            [genuine-highlighter
             [core :as hl]]
            [genuine-highlighter.rendering-rules [terminal :as t]]
            [leiningen.highlight.rendering-rules.html :as html]
            [compojure [core :refer [defroutes GET]]
                       [route :as route]]
            [ring.adapter.jetty :as jetty]
            [hiccup [page :as hiccup]]))

(defn- drop-proceeding-newlines [s]
  (clojure.string/replace s #"^\n+" ""))

(defn- read-and-highlight [^java.io.BufferedReader in]
  (loop [code ""]
    (let [line (.readLine in)
          code' (str code \newline line)]
      (if-let [result (hl/highlight t/colorful-symbols-rule code' :suppress-eval? true)]
        (let [result (->> (clojure.string/split result #"\n")
                          (drop-while #(= "" %))
                          (map #(str "  #_== " %))
                          (clojure.string/join \newline))]
          (println result)
          (flush)
          (read-string code'))
        (do (print "  #_=> ")
            (flush)
            (recur code'))))))

(defn- html-template [nsname body]
  (hiccup/html5
    [:head
      [:title nsname]
      [:link {:href "/css/highlight.css", :rel "stylesheet", :type "text/css"}]
      [:script {:src "https://code.jquery.com/jquery-2.1.4.min.js"}]
      [:script {:src "/js/highlight.js"}]]
    [:body
      [:h1 nsname]
      body]))

(defn- render-html [nsname code]
  (let [lines (str/split code #"\n")
        num (count lines)]
    (html-template nsname
     `[:div.file
       [:table.file-code
        [:tbody
         [:tr
          [:td.line-nums
           ~@(for [i (range 1 (inc num))]
               [:span {:id (str \L i), :rel (str "#L" i)} i])]
          [:td.line-code
           [:div.highlight
            [:pre
             ~@(for [[i line] (map list (range 1 (inc num)) lines)
                     :let [line (if (= line "") "<br>" line)]]
                 [:div.line {:id (str "LC" i)} line])]]]]]]])))

(defn- handler [nsname]
  (when-let [resource (-> (#'clojure.core/root-resource nsname)
                          (str/replace #"^/" "")
                          (str ".clj")
                          io/resource)]
    (->> (hl/highlight html/rendering-rule
                       (slurp resource)
                       :ns (create-ns (symbol nsname)))
         (render-html nsname))))

(defroutes app
  (GET "/ns/:nsname" [nsname]
    (handler nsname))
  (route/resources "/")
  (route/not-found "Not found"))

(defn- browse-highlighted-namespace [nsname]
  (if nsname
    (try
      (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))
            server (jetty/run-jetty app {:port port, :join? false})]
        (browse/browse-url (str "http://localhost:" port "/ns/" nsname))
        (.join server)))
    (throw (Exception. "specify namespace to be highlighted"))))

(defn highlight
  "Highlight Clojure source code."
  ([project] (highlight project "repl"))
  ([project command & args]
     (case command
       "browse"
       #_=> (browse-highlighted-namespace (first args))
       "repl"
       #_=> (m/repl :read (fn [_ _] (read-and-highlight (io/reader *in*)))
                    :need-prompt #(do true))
       #_else (throw (Exception. (str "unknown subcommand: " command))))))
