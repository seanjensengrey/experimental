(ns algorithms.sorting.insertion
  (:use [algorithms.util.helper]))

;; ----------------------------------------------
;; Insertion Sort
;; Don't ever actually use this code
;; ----------------------------------------------


(defn insertion-sort
  "Test insertion sort impl. Do not use in real code.
   i=0               k=9
  ---------------------
  |0|1|2|3|4|5|6|7|8|9|
  ---------------------
     j=1
     tmp=1
  "
  [data]
  (let [k (- (count data) 1)]
    (loop [j 1 t (transient (vec data))]
      (do
;        (print-vector t)
;        (println)
        (if (> j k)
          (persistent! t)
          (let [tmp (nth t j)]
            (recur
              (inc j)
              (loop [i (- j 1)]
                (if (< i 0)
                  t
                  (recur
                    (if (> (nth t i) (nth t (+ i 1)))
                      (do
                        (let [tmp (nth t (+ i 1))]
                          (assoc! t (+ i 1) (nth t i))
                          (assoc! t i tmp))
                          (dec i))
                      -1)))))))))))
