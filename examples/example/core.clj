(ns example.core
  (:require [example.lib :as lib]))

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
