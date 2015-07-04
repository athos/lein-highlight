(defproject lein-highlight "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [genuine-highlighter "0.1.0-SNAPSHOT"]
                 [compojure "1.3.4" :exclusions [instaparse]]
                 [instaparse "1.4.1"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [ring/ring-devel "1.3.2"]
                 [hiccup "1.0.5"]]
  :resource-paths ["resources"]
  :profiles {:1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :dev {:dependencies [[org.clojure/tools.namespace "0.2.10"]]
                   :source-paths ["src" "dev" "examples"]}}
  :aliases {"all" ["with-profile" "dev:1.5:1.6"]}
  :eval-in-leiningen true)
