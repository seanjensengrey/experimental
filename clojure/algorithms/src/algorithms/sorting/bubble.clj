(ns algorithms.sorting.bubble)

;; ----------------------------------------------
;; Debugging macros
;; ----------------------------------------------
;; uncomment this macro to turn on logging
;(defmacro dbg [x] `(let [x# ~x res# x#] (println "dbg:" '~x "=" res#) res#))

;; comment out this macro and uncomment the one above to turn on logging
(defmacro dbg [x] `(let [x# ~x] x#))


;; ----------------------------------------------
;; BubbleSort : No optimizations
;; Don't ever actually use this code
;; ----------------------------------------------
(defn compare-and-swap
  "data is transient, swap if left is greater than right"
  [data left right]
  (let [a (nth data left)
        b (nth data right)]
    (dbg
      (if (> a b)
        (assoc! (assoc! data left b) right a)
        data))))

; (nth data i) (nth data (+ i 1)
(defn bubble-sort-single-pass
  "data is transient, k is the length of this pass"
  [data k]
  (loop [i 0]
    (if (>= i k)
      data
      (recur (do
        (compare-and-swap data i (+ i 1))
        (inc i))))))

(defn bubble-sort
  "Test bubble sort impl (do not use). Converts the input vector into a
  transient vector, and performs in-place swapping."
  [data]
  (loop [t (transient (vec data))
         k (- (count data) 1)]
    (if (<= k 0)
      (persistent! t)
      (recur (bubble-sort-single-pass t k) (dec k)))))

;(prn (bubble-sort [1 9 4 8 9 2]))


;; ----------------------------------------------
;; BubbleSort : ShortCircuit
;; Don't ever actually use this code
;; ----------------------------------------------
(defn compare-and-swap-with-signal
  "data is transient, swap if left is greater than right"
  [swapped data left right]
  (let [a (nth data left)
        b (nth data right)]
    (dbg
      (if (> a b)
        (do
          (assoc! (assoc! data left b) right a)
          true)
        (or swapped false)))))

(defn bubble-sort-short-circuit
  "Test bubble sort impl (do not use). Short circuits if no swaps have been performed."
  [data]
  (loop [t (transient (vec data))
         k (- (count t) 1)
         active true]
    (if (= active true)
      (let [res
            (loop [i 0 swapped false]
              (if (>= i k)
                swapped
                (recur (inc i) (compare-and-swap-with-signal swapped t i (+ i 1)))))]
        (recur t (dec k) res))
      (persistent! t))))

;(prn (bubble-sort-short-circuit [1 9 4 8 9 2]))




























































