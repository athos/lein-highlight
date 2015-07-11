(ns leiningen.highlight.rendering-rules.html
  (:require [hiccup.core :as hiccup]
            [clojure.string :as str]))

(defn- wrap-span [classes attrs content]
  (let [classes (str/join \space classes)]
    (hiccup/html
      [:span (merge {:class classes} attrs) content])))

(defn- var-link [var content]
  (let [m (meta var)]
    [:a {:href (format "/ns/%s#%s" (str (:ns m)) (:name m))}
     content]))

(defn- symbol-attr [x]
  {:data-symbol x})

(defn ^:private colorful-symbol [x v]
  (when-let [info (some-> x :symbol-info)]
    (let [type (name (:type info))]
      (case type
        "local"
        #_=> (if (= (:usage info) :def)
               (wrap-span [type "def"] (symbol-attr (:id x))
                          [:a {:name (:id x)} v])
               (wrap-span [type] (symbol-attr (:binding info))
                          [:a {:href (str "#" (:binding info))} v]))
        "var"
        #_=> (if (= (:usage info) :def)
               (wrap-span [type "def"] (symbol-attr (:name info))
                          [:a {:name (:name info)} v])
               (wrap-span [type] (symbol-attr v)
                          (var-link (:var info) v)))
        "macro"
        #_=> (wrap-span [type] (symbol-attr (:macro info))
                        (var-link (:macro info) v))
        #_else (wrap-span [type] {} v)))))

(def colorful-symbols-rule
  {:symbol {:content colorful-symbol}})

(def keyword-rule
  {:keyword {:content (fn [x v] (wrap-span ["keyword"] {} v))}})
