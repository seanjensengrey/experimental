(ns algorithms.bst.hashtree
  (:use algorithms.bst.protocols))

;; ----------------------------------------------
;; Helper methods
;; ----------------------------------------------
(defn left_child_index [index] (* 2 index))
(defn right_child_index [index] (+ 1 (* 2 index)))

;; ----------------------------------------------
;; A Generic Node
;;
;; Note: no need for the left, right, or parent node pointers as they can be calculated from the
;; current key.
;; ----------------------------------------------
(defrecord Node [key data]
  java.lang.Object
  (toString [this] (str "{key: " (:key this) ", data: " (:data this) "}")))
(defn create-Node [key data] (Node. key data))

;; ----------------------------------------------
;; HashTreeNode
;; ----------------------------------------------
(defrecord HashTree [table]

  BINARYTREE

  (root [this] (table 1))
  (insert [this node]
    (do (dbg (str "INSERT this: " this ", node: " node))
      (loop [index 1]
        (let [cur ((:table this) index)]
          (if (nil? cur)
            (dbg (assoc-in this [:table index] node))
            (recur
              (if (< (:key node) (:key cur))
                (dbg (left_child_index index))
                (dbg (right_child_index index)))))))))
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
          ))))
  ;; Override the toSting if we wont
  ;  java.lang.Object
  ;  (toString [this] (str (:key this)))
  )

;; ----------------------------------------------
;; HashTreeNode Factory
;;
;; Used by the consumers (via :use) of algorithms.bst.hashtree
;; ----------------------------------------------
(defn create-HashTree [] (HashTree. {}))

