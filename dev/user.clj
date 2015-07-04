(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [leiningen.highlight :as hl]
            [ring.server.standalone :refer [serve]]))

(def system nil)

(defn start [port]
  (let [port (if port (Long/parseLong port) 3000)
        opts {:port port :open-browser? false}]
    (alter-var-root #'system (constantly (serve #'hl/app opts)))))

(defn stop []
  (.stop system)
  (alter-var-root #'system (constantly nil)))

(defn go [& [port]]
  (start port))

(defn reset []
  (stop)
  (refresh :after 'user/go))

