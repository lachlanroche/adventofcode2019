(ns aoc20.day13
  (:require [instaparse.core :as insta]))

(defn input-data
  []
  (let [s (->> "aoc/day18.txt"
               clojure.java.io/resource
               slurp
               clojure.string/split-lines
               #_(map #(clojure.string/split % #" "))
               )]
    s))

(def arithmetic
  (insta/parser
    "expr = add-mul
     <add-mul> = term | add | mul
     add = add-mul <'+'> term
     mul = add-mul <'*'> term
     <term> = number | <'('> add-mul <')'>
     number = #'[0-9]+'"
    :auto-whitespace :standard))

(defn part1
  []
  (reduce
   (fn [acc expr]
     (+ acc
        (->> (arithmetic expr)
             (insta/transform parse-tree->sexp))))
   0
   (input-data)))


