(ns algorithms.util.helper)

(defn print-vector [v]
  (let [c (count v)]
    (loop [i 0]
      (when (< i c)
        (pr (str (nth v i) " "))
        (recur (inc i))))))
