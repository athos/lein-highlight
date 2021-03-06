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

(defn- concat-elems [& elems]
  (hiccup/html `[:span ~@elems]))

(defn ^:private symbol-rule [x v]
  (when-let [info (some-> x :symbol-info)]
    (let [type (name (:type info))]
      (case type
        "local"
        #_=> (if (= (:usage info) :def)
               (wrap-span [type "def"] (symbol-attr (:id x))
                          [:a {:name (:id x)} (str v)])
               (wrap-span [type] (symbol-attr (:binding info))
                          [:a {:href (str "#" (:binding info))} v]))
        "var"
        #_=> (if (= (:usage info) :def)
               (wrap-span [type "def"] (symbol-attr (:name info))
                          [:a {:name (:name info)} (str v)])
               (wrap-span type (symbol-attr v)
                          (var-link (:var info) v)))
        "macro"
        #_=> (let [macro-name (str/replace (str (:macro info)) #"^#'" "")]
               (wrap-span type (symbol-attr macro-name)
                          (var-link (:macro info) v)))
        "class"
        #_=> (let [cstr (str v)]
               (if (.endsWith cstr ".")
                 (concat-elems
                   (wrap-span type (subs cstr 0 (dec (count cstr))))
                   (wrap-span "special" "."))
                 (wrap-span type v)))
        "member"
        #_=> (let [mstr (str v)]
               (if (.startsWith mstr ".")
                 (concat-elems
                   (wrap-span "special" ".")
                   (wrap-span type (subs mstr 1)))
                 (if-let [[_ c m] (re-matches #"([^/]+)/([^/]+)" mstr)]
                   (concat-elems
                     (wrap-span "class" c)
                     (wrap-span "special" "/")
                     (wrap-span type m))
                   (wrap-span type v))))
        #_else (wrap-span type (str v))))))

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
   :regex {:content #(wrap-span "regex" %2)}
   :string {:content string-rule}
   :char {:content #(wrap-span "char" %2)}
   :quote {:quote #(wrap-span "quote" %2)}
   :var {:var #(wrap-span "var-quote" %2)}
   :meta {:meta #(wrap-span "meta" %2)}
   :deref {:deref #(wrap-span "deref" %2)}
   :comment {:content #(wrap-span "comment" %2)}
   :discard {:discard #(wrap-span "comment" %2)}
   :syntax-quote {:syntax-quote #(wrap-span "syntax-quote" %2)}
   :unquote {:unquote #(wrap-span "unquote" %2)}
   :unquote-splicing {:unquote-splicing #(wrap-span "unquote-splicing" %2)}
   :list (collection-rule "list")
   :vector (collection-rule "vector")
   :map (collection-rule "map")
   :set (collection-rule "set")
   :fn (collection-rule "fn")})
