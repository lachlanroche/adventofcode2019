(ns aoc.day02)

(defn op-add
  [a b]
  (+ a b))

(defn op-mul
  [a b]
  (* a b))

(defn op-halt
  [_ _]
  nil)

(def op-map
  {1 op-add
   2 op-mul
   99 op-halt
   })

(defn step
  [mem offset]
  (let [[op-code a b c] (vec (take 4 (drop offset mem)))
        op-fn (get op-map op-code op-halt)
        a (get mem a)
        b (get mem b)
        result (op-fn a b)]
    (if (nil? result)
      result
      [c result])))

(defn program
  ([mem]
   (program mem 0))
  ([mem offset]
   (let [result (step mem offset)]
     (if (nil? result)
       mem
       (recur (apply assoc mem result) (+ 4 offset))))))

(step [1,0,0,0,99] 0)
(step [2,0,0,0,99] 0)
(step [2,0,0,0,99] 4)
(program [1,0,0,0,99])
(program [2,3,0,3,99])
(program [2,4,4,5,99,0])
(program [1,1,1,4,99,5,6,0,99])
