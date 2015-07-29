(ns example.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [example.lib :as lib]))

(defn collatz [n]
  (lazy-seq
    (cons n
          (if (lib/even? n)
            (collatz (/ n 2))
            (collatz (+ (* 3 n) 1))))))

(defmacro let1 [name expr & body]
  `(let [~name ~expr]
     ~@body))

(def x 42)

(defn f []
  (let1 x (* x x)
    (+ x x)))

(->> (str/split "hoge\nfuga\npiyo" #"\n")
     (map #(str "[" % "]"))
     (str/join \newline))

(defmacro aif [test then else]
  `(let [~'it ~test]
     ~then
     ~else))

(defn g [x]
  (aif (:k x)
    (inc it)
    0))

(let [x 0]
  (let [[x & y] [1 2 3]]
    (let [x x]
      x)))

(defn f [x]
  (with-open [x (io/reader "hoge")]
    x))

'{:tag String}x
