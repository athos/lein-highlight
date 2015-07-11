(ns leiningen.highlight.rendering-rules.html
  (:require [clojure.string :as str]))

(defn- wrap-span [classes content]
  (if (coll? classes)
    (let [classes (str/join \space classes)]
      (format "<span class=\"%s\">%s</span>" classes content))
    (format "<span class=\"%s\">%s</span>" classes content)))

(defn- var-link [var content]
  (let [m (meta var)]
    (format "<a href=\"/ns/%s#%s\">%s</a>" (str (:ns m)) (:name m) content)))

(defn- symbol-class [x]
  (str "sym__" x))

(defn ^:private colorful-symbol [x v]
  (when-let [info (some-> x :symbol-info)]
    (let [type (name (:type info))]
      (case type
        "local"
        #_=> (if (= (:usage info) :def)
               (->> (format "<a name=\"%s\">%s</a>" (:id x) v)
                    (wrap-span [type (symbol-class (:id x))]))
               (->> (format "<a href=\"#%s\">%s</a>" (:binding info) v)
                    (wrap-span [type (symbol-class (:binding info))])))
        "var"
        #_=> (->> (if (= (:usage info) :def)
                    (format "<a name=\"%s\">%s</a>" (:name info) v)
                    (var-link (:var info) v))
                  (wrap-span [type (symbol-class (name v))]))
        "macro"
        #_=> (wrap-span [type (symbol-class (:macro info))]
                        (var-link (:macro info) v))
        #_else (wrap-span type v)))))

(def colorful-symbols-rule
  {:symbol {:content colorful-symbol}})

(def keyword-rule
  {:keyword {:content (fn [x v] (wrap-span "keyword" v))}})
