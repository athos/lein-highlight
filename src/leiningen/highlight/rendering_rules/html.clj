(ns leiningen.highlight.rendering-rules.html)

(defn- wrap-span [class content]
  (format "<span class=\"%s\">%s</span>" class content))

(defn- var-link [var content]
  (let [m (meta var)]
    (format "<a href=\"/ns/%s#%s\">%s</a>" (str (:ns m)) (:name m) content)))

(defn ^:private colorful-symbol [x v]
  (when-let [info (some-> x :symbol-info)]
    (let [type (name (:type info))]
      (case type
        "local"
        #_=> (->> (if (= (:usage info) :def)
                    (format "<a name=\"%s\">%s</a>" (:id x) v)
                    (format "<a href=\"#%s\">%s</a>" (:binding info) v))
                  (wrap-span type))
        "var"
        #_=> (->> (if (= (:usage info) :def)
                    (format "<a name=\"%s\">%s</a>" (:name info) v)
                    (var-link (:var info) v))
                  (wrap-span type))
        "macro"
        #_=> (wrap-span type (var-link (:macro info) v))
        #_else (wrap-span type v)))))

(def colorful-symbols-rule
  {:symbol {:content colorful-symbol}})

