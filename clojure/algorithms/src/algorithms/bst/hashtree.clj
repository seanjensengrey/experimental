(ns algorithms.bst.hashtree
  (:use algorithms.bst.protocols))

;; ----------------------------------------------
;; HashTreeNode
;; ----------------------------------------------
(defn left_child_index [index] (* 2 index))
(defn right_child_index [index] (+ 1 (* 2 index)))

;; no need for the left, right, or parent node pointers as they can be calculated from the current key
(defrecord Node [key data]
  java.lang.Object
  (toString [this] (str "{key: " (:key this) ", data: " (:data this) "}")))

;"TODO: use Meikel's coolness to create the internal map w/o having to specify it."
(defrecord HashTree [table]

  BINARYTREE

  (root [this] (table 1))
  (insert [this node]
    (do (println (str "INSERT this: " this ", node: " node))
      (loop [index 1]
        (let [cur ((:table this) index)]
          (if (nil? cur)
            (dbg (assoc-in this [:table index] node))
            (recur
              (if (< (:key node) (:key cur))
                (dbg (left_child_index index))
                (dbg (right_child_index index)))))))))
  ;  java.lang.Object
  ;  (toString [this] (str (:key this)))
  (delete [this node]
    (loop [index 1]
      (let [cur ((:table this) index)]
        (if (= (:key node) (:key cur))
          (assoc this :table (dissoc (:table this) (:key node)))
          (recur
            (if (< (:key node) (:key cur))
              (left_child_index index)
              (right_child_index index)))))))
  (search [this k]
    (loop [index 1]
      (let [cur ((:table this) index)]
        (if (= k (:key cur))
          cur
          (recur
            (if (< k (:key cur))
              (left_child_index index)
              (right_child_index index)))))))
  (minimum [this]
    (loop [index 1]
      (let [cur ((:table this) index)
            left ((:table this) (left_child_index index))]
        (if (nil? left)
          cur
          (recur (left_child_index index))))))
  (maximum [this]
    (loop [index 1]
      (let [cur ((:table this) index)
            right ((:table this) (right_child_index index))]
        (if (nil? right)
          cur
          (recur (right_child_index index))
          )))))

