(ns leiningen.highlight.rendering-rules.html
  (:require [hiccup.core :as hiccup]
            [clojure.string :as str]))

(defn- wrap-span
  ([classes content]
   (wrap-span classes {} content))
  ([classes attrs content]
   (let [class (if (coll? classes)
                 (str/join \space classes)
                 classes)]
     (hiccup/html
      [:span (merge {:class class} attrs) content]))))

(defn- var-link [var content]
  (let [m (meta var)]
    [:a {:href (format "/ns/%s#%s" (str (:ns m)) (:name m))}
     (str content)]))

(defn- symbol-attr [x]
  {:data-symbol x})

(defn ^:private symbol-rule [x v]
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
               (wrap-span type (symbol-attr v)
                          (var-link (:var info) v)))
        "macro"
        #_=> (wrap-span type (symbol-attr (:macro info))
                        (var-link (:macro info) v))
        #_else (wrap-span type v)))))

(defn string-rule [x v]
  (->> (str/split v #"\n")
       (map #(wrap-span "string" %))
       (str/join \newline)))

(defn collection-rule [coll-name]
  {:open #(wrap-span (str coll-name "-open") %2)
   :close #(wrap-span (str coll-name "-close") %2)})

(def rendering-rule
  {:symbol {:content symbol-rule}
   :nil {:content #(wrap-span "nil" %2)}
   :boolean {:content #(wrap-span "boolean" %2)}
   :number {:content #(wrap-span "number" %2)}
   :keyword {:content #(wrap-span "keyword" %2)}
   :string {:content string-rule}
   :comment {:content #(wrap-span "comment" %2)}
   :list (collection-rule "list")
   :vector (collection-rule "vector")
   :map (collection-rule "map")
   :set (collection-rule "set")
   :fn (collection-rule "fn")})
