(ns aoc20.day11)

(defn input-data
  []
  (let [s (->> "aoc/day11.txt"
               clojure.java.io/resource
               slurp
               )
        ]
    (loop [s s x 0 y 0 canvas {} size [0 0]]
      (if (empty? s)
        {:canvas canvas :size size}
        (let [ch (first s)
              seat? (= \L ch)
              [max-x max-y] size
              canvas (if (not seat?) canvas (assoc canvas [x y] \L))]
          (cond
            (= ch \newline)
            (recur (rest s) 0 (inc y) canvas size)
            :else
            (let [size [(max max-x x) (max max-y y)]]
              (recur (rest s) (inc x) y canvas size))))))))

(defn neighbors
  [_ xy]
  (if (nil? xy)
    []
    (let [[x y] xy]
      [
       [(dec x) (dec y)]
       [     x (dec y)]
       [(inc x) (dec y)]
       [(dec x)      y]

       [(inc x)      y]
       [(dec x) (inc y)]
       [      x (inc y)]
       [(inc x) (inc y)]
       ])))

(defn step-canvas
  [canvas threshold neighbors-fn]
  (loop [result {} xys (keys canvas)]
    (let [xy (first xys)
          xys (rest xys)
          friends (->>
                       (select-keys canvas (neighbors-fn canvas xy))
                       (filter #(= \# (val %)))
                       count)]
      (cond
        (nil? xy)
        result
        (and (= \L (get canvas xy))
             (= 0 friends))
        (recur (assoc result xy \#) xys)
        (and (= \# (get canvas xy))
             (>= friends threshold))
        (recur (assoc result xy \L) xys)
        :else
        (recur (assoc result xy (get canvas xy)) xys)))))

(defn canvas-count
  [canvas]
  (->> canvas
       (filter #(= (val %) \#))
       count))

(defn part1
  []
  (let [world (input-data)]
    (loop [canvas (:canvas world)]
      (let [next-canvas (step-canvas canvas 4 neighbors)]
        (if (not= canvas next-canvas)
          (recur next-canvas)
          (canvas-count next-canvas))))))

(defn visible-neighbor-direction
  [[max-x max-y] canvas [x y] [dx dy]]
  (loop [x (+ x dx) y (+ y dy)]
    (let [pt (get canvas [x y])]
      (cond
        (or (> 0 x)
            (> 0 y)
            (< max-x x)
            (< max-y y))
        [(inc max-y) (inc max-y)]
        (nil? pt)
        (recur (+ x dx) (+ y dy))
        :else
        [x y]))))

(defn visible-neighbors
  [size canvas xy]
  (if (nil? xy)
    []
    (list
          (visible-neighbor-direction size canvas xy [1 1])
          (visible-neighbor-direction size canvas xy [0 1])
          (visible-neighbor-direction size canvas xy [-1 1])
          (visible-neighbor-direction size canvas xy [1 0])
          (visible-neighbor-direction size canvas xy [-1 0])
          (visible-neighbor-direction size canvas xy [1 -1])
          (visible-neighbor-direction size canvas xy [0 -1])
          (visible-neighbor-direction size canvas xy [-1 -1]))))

(defn part2
  []
  (let [world (input-data)]
    (loop [canvas (:canvas world)]
      (let [next-canvas (step-canvas canvas 5 (partial visible-neighbors (:size world)))]
        (if (not= canvas next-canvas)
          (recur next-canvas)
          (canvas-count next-canvas))))))
