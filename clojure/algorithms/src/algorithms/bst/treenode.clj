(ns algorithms.bst.treenode
  (:use algorithms.bst.protocols))

;; ----------------------------------------------
;; TreeNode
;; ----------------------------------------------
(defn unwind [node stack]
  (reduce
    #(if (< (:key %1) (:key %2))
      (assoc %2 :left %1)
      (assoc %2 :right %1))
    node stack))


(defrecord TreeNode
    "Tree implementation of a BST. Each node has a pointer to a left and right child. Note that there
    is no parent pointer. This is b/c that would force the entire tree to be re-built for each node
    addition and deletion. In this impl, only the path from the node back up to the root mist be re-built."
  [left right key]

  BINARYTREE
  (root [this] this)
  (insert [this node]
    (loop [current this
           stack []]
      (if (= current nil)
        (unwind node stack)
        (recur
          (if (< (:key node) (:key current))
            (:left current)
            (:right current))
          (cons current stack)))))
  (delete [this node]
    (loop [current this
           stack []]
      (if (= (:key node) (:key current))
        (unwind nil stack)
        (recur
          (if (< (:key node) (:key current))
            (:left current)
            (:right current))
          (cons current stack)))))
  (search [this key]
    (loop [current this]
      (if (= key (:key current))
        current
        (recur
          (if (< key (:key current))
            (:left current)
            (:right current))))))
  (minimum [this]
    (loop [stack (list this)
           current this]
      (if (nil? current)
        (first stack)
        (recur (cons current stack) (:left current)))))
  (maximum [this]
    (loop [stack (list this)
           current this]
      (if (nil? current)
        (first stack)
        (recur (cons current stack) (:right current)))))
  java.lang.Object
  (toString [this] (str "{key: " (:key this) ", left: " (:left this) ", right: " (:right this) "}")))

(defn create-TreeNode [left right key] (TreeNode. left right key))

(sorted-map)


;; ----------------------------------------------
;; Q: I don't see how to make these traversals work
;; on the TreeNode datastructure.
;; ----------------------------------------------

;(defrecord TreeGrapher
;  TRAVERSAL
;  (preorder [this tree vf]
;    "walk the tree using 'preorder' traversal, and invoke the visitor function 'vf' on each node."
;    (loop [stack (list this)]
;      (if (= 0 (count stack))
;        tree
;        (recur
;          (if (not (nil? cur))
;            (let [cur (first stack)]
;              (do
;                ;; visitor function ;;
;                (vf cur)
;                ;; pull the first element off the stack, as it's been used
;                ;; and push the right, then left nodes onto the stack
;                ;; for processing in subsequent iterations
;                (conj (rest stack) (:right cur) (:left cur))))
;            ;; node was nill, so just pop it off the stack
;            (rest stack))))))
;
;  (inorder [this tree vf]
;    "walk the tree using 'inorder' traversal, and invoke the visitor function 'vf' on each node."
;    (loop [stack (list this)]
;      (if (= 0 (count stack))
;        tree
;        (recur
;          (let [cur (first stack)]
;            (do (vf cur)
;              (conj stack (:right cur) (:left cur))))))))
;;  )


