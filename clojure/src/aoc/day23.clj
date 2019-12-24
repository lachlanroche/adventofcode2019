(ns aoc.day23
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [aoc.day09 :as ic]
            [clojure.math.combinatorics :as combo]))

(defn program-step
  [mem in out offset rbase idle?]
  (let [op (ic/cpu-decode (get mem offset))
        op-fn (:fn op)
        op-argc (:argc op)
        op-mode (:mode op)
        idle? (cond
                (= ic/op-output op-fn)
                false
                (and (= ic/op-output op-fn) (empty? in))
                true
                :else
                idle?)
        ]
    (cond
      (= ic/op-halt op-fn)
      [mem in out offset rbase idle?]
      (and (= ic/op-input op-fn) (empty? in))
      (let [[mem in out offset rbase] (op-fn (vec (take op-argc (drop (+ 1 offset) mem))) op-mode [mem [-1] out offset rbase])]
        [mem in out offset rbase idle?])
      :else
      (let [[mem in out offset rbase] (op-fn (vec (take op-argc (drop (+ 1 offset) mem))) op-mode [mem in out offset rbase])]
        [mem in out offset rbase idle?]))))

(defn network-idle?
  [computers]
  (every? #(and (empty? (second %)) (last %)) computers))

(defn load-program
  []
  (-> "aoc/day23.txt"
      io/resource
      slurp
      ic/program-compile))

(defn make-world
  [n]
  (let [prog (load-program)]
    (vec (map #(vector prog [%] [] 0 0 false) (range n)))))

(defn route-nat-1
  [computers nat b c]
  [nil nil c])

(defn route-packets
  [computers nat route-nat-fn]
  (let [has-packet? #(= 3 (count (nth % 2)))
        rm-packet #(if (has-packet? %) (assoc % 2 []) %)
        packets (->> computers
                     (filter has-packet?)
                     (map #(nth % 2))
                     concat
                     )
        computers (->> computers
                       (map rm-packet)
                       vec)]
    (loop [packets packets computers computers]
      (if (empty? packets)
        [computers nat nil]
        (let [[a b c] (first packets)]
          (if (= 255 a)
            (let [[computers nat result] (route-nat-fn computers nat b c)]
              (if (not (nil? result))
                [nil nil result]
                (recur (rest packets) computers)))
            (let [comp (get computers a)
                  in (->
                      (get comp 1 [])
                      (conj b)
                      (conj c))
                  comp (assoc comp 1 in)
                  computers (assoc computers a comp)]
              (recur (rest packets) computers))))))))

(defn run-loop
  [computers nat route-nat-fn result]
  (cond
    (number? result)
    result
    (every? #(ic/program-stopped? (first %) (nth % 3)) computers)
    nil
    :else
    (let [computers (map #(apply program-step %) computers)
          [computers nat result] (route-packets computers nat route-nat-fn)]
      (if (nil? result)
        (recur computers nat route-nat-fn result)
        result))))


(defn part1
  []
  (run-loop (make-world 50) nil route-nat-1 nil))