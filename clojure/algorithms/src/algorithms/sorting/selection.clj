(ns algorithms.sorting.selection
  (:use [algorithms.util.helper]))

;; ----------------------------------------------
;; Selection Sort
;; Don't ever actually use this code
;; ----------------------------------------------

;ERROR in (time-selectionsort) (LazySeq.java:47)
;Uncaught exception, not in assertion.
;expected: nil
;  actual: java.lang.RuntimeException: java.lang.IllegalArgumentException: Don't know how to create ISeq from: clojure.lang.PersistentVector$TransientVector
;(defn find-min-val [t]
;  (reduce min t))

(defn find-min-val [t start k]
    (loop [i start
           m i]
      (if (> i k)
        m
        (recur
          (inc i)
          (if  (< (nth t i) (nth t m))
            i ;; update m to be 'i'
            m ;; let m stay the same
                      )))))

    (defn swap [t a b]
      (let [tmp (nth t b)]
        (do
          (assoc! t b (nth t a))
          (assoc! t a tmp)
          t)))

    (defn selection-sort
      "Test insertion sort impl. Do not use in real code.
       i=0               k=9
      ---------------------
      |0|1|2|3|4|5|6|7|8|9|
      ---------------------
      Find the min value from i+1 to k, and swap with i.
      "
      [data]
      (let [k (- (count data) 1)]
        (loop [i 0 t (transient (vec data))]
          (if (>= i k)
            (persistent! t)
            (do
;              (print-vector t) (println)
              (swap t i (find-min-val t i k))
              (recur (inc i) t))))))
