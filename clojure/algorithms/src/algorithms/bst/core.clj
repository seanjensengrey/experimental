(ns algorithms.core
  (:use clojure.test))

;; -----------------------------------------------------------------------------
;; A simple BST implemented in Clojure using both defrecord and defprotocol.
;; Note the use of the unwind function to update the graph after each insertion.
;;
;; References:
;;
;; BST/Clojure
;; http://nflath.com/2009/07/avl-tree-implementation-in-clojure/
;; https://secure.wikimedia.org/wikipedia/en/wiki/Binary_search_tree
;;
;; Recommends against using clojure to write a bst, use sorted-map or sorted-set instead
;; http://stackoverflow.com/questions/1611157/how-do-you-make-a-binary-search-tree-in-clojure
;;
;; Processing large binary files in clojure:
;; http://efreedom.com/Question/1-3538834/Process-Large-Binary-Data-Clojure
;;
;; Clojure Protocols:
;; http://freegeek.in/blog/2010/05/clojure-protocols-datatypes-a-sneak-peek/
;;
;; Lisp Binary Search Tree
;; http://digital.cs.usu.edu/~vkulyukin/vkweb/software/bst/bst.html
;; http://www.comanswer.com/question/help-with-lisp-code-for-a-binary-tree
;; http://www.cs.sfu.ca/CC/310/pwfong/Lisp/3/tutorial3.html
;;
;; Clojure.zip
;; http://clojure.org/other_libraries
;; http://www.exampler.com/blog/2010/09/01/editing-trees-in-clojure-with-clojurezip/
;;
;; Clojure 'cond'
;; http://code-redefined.blogspot.com/2010/02/else-clause-in-clojure-cond.html
;; http://clojuredocs.org/clojure_core/clojure.core/cond
;;
;; Cloure defrecord default contstructor arguments
;; http://www.mail-archive.com/clojure@googlegroups.com/msg33291.html
;; http://clojuredocs.org/clojure_core/clojure.core/defrecord
;; http://cemerick.com/2010/08/02/defrecord-slot-defaults/
;;
;; Clojure hash-map vs. sorted-map key behavior
;; https://groups.google.com/group/clojure/browse_thread/thread/ce3e58879e4724a3
;; http://download.oracle.com/javase/1.4.2/docs/api/java/util/TreeMap.html
;;
;; Clojure defrecord / map
;; http://stackoverflow.com/questions/3744349/how-do-i-get-core-clojure-functions-to-work-with-my-defrecords
;;
;; Protocols video by Stuart Halloway
;; http://vimeo.com/11236603
;;
;; Debugging macros
;; http://stackoverflow.com/questions/2352020/debugging-in-clojure
;; -----------------------------------------------------------------------------

;;debugging parts of expressions
(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))


;; ----------------------------------------------
;; Protocols
;; ----------------------------------------------
(defprotocol BINARYTREE
  (root [this])
  (insert [this node])
  (delete [this node])
  (search [this key])
  (minimum [this])
  (maximum [this]))

(defprotocol TRAVERSAL
  (inorder [this tree visitor])
  (preorder [this tree visitor])
  (postorder [this tree visitor]))

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
  ;  "Tree implementation of a BST. Each node has a pointer to a left and right child. Note that there
  ;  is no parent pointer. This is b/c that would force the entire tree to be re-built for each node
  ;  addition and deletion. In this impl, only the path from the node back up to the root mist be re-built."
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


;; ----------------------------------------------
;; Visual testing/confirmation that this works
;; ----------------------------------------------

;; create test data
(def test-data (list 10 5 20 7 4 40 50))

;; create tree node to test
(def test-tree-node
  (loop [t (TreeNode. nil nil 50)
         data test-data]
    (if (= 0 (count data))
      t
      (recur
        (insert t (TreeNode. nil nil (first data)))
        (rest data)))))

;; ----------------------------------------------
;; Test Helpers :: TODO: move these to separate file
;; ----------------------------------------------

(defn message [m]
  (do
    (println "----------------------------------------------------")
    (println m)
    (println "----------------------------------------------------")))

(defn visual-tree-test [t s]
  (do
    (message (str "Testing " s))
    (println "tree: " t)
    (println "tree (toString): " (str t))
    (println (str "minimum: " (minimum t)))
    (println (str "maximum: " (maximum t)))
    (println (str "search for 5: " (search t 5)))
    ))

(visual-tree-test test-tree-node "TreeNode")

;; ----------------------------------------------
;; Tree Node: Tests
;; ----------------------------------------------

(deftest test_basic_tree
  (is (= 4 (:key (minimum test-tree-node))))
  (is (= 50 (:key (maximum test-tree-node)))))


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

;; ----------------------------------------------
;; Hash Tree : Visual testing/confirmation that this works
;; ----------------------------------------------
;(def test-hash-tree
;  (loop [ t (HashTree. {} )
;          data test-data ]
;    (if (= 0 (count data))
;      t
;      (recur nil (rest data)))))

;  (loop [ t (HashTree. {})
;          data test-data ]
;    (if (= 0 (count data))
;      t
;      (recur
;        (insert t (Node. (first data) (first data)))
;        (rest data)))))

;(visual-tree-test test-hash-tree "HashTree")

(message "Testing HashTreeNode")

(def htree (HashTree. {}))
(prn "htree: " htree)
(def n (Node. 99 99))
(prn "n: " n)
(def ht1 (insert htree n))
(prn "ht1: " ht1)
(def ht2 (insert ht1 (Node. 98 98)))
(prn "ht2: " ht2)
(def ht3 (insert ht2 (Node. 97 97)))
(prn "ht3: " ht3)
(def ht4 (insert ht3 (Node. 96 96)))
(prn "ht4: " ht4)

;(defrecord bar [a])
;
;(def test-loop
;  (loop [foo (dbg (bar. 1))
;         data (list 2 3 4)]
;    (if (= 0 (count data))
;      foo
;      (recur (dbg (bar. (first data))) (rest data)))))
;
;(println test-loop)

;; create test data
(def test-hash-tree
  (loop [tht (dbg (HashTree. {}))
         data (dbg (list 0 1))]
    (if (= 0 (count data))
      (dbg tht)
      (recur
        (dbg (insert tht (Node. (first data)(first data))))
        (dbg (rest data))))))

;(println "test-hash-tree: " test-hash-tree)
;(println "test-hash-tree: " (str test-hash-tree))
;(println (str "minimum: " (minimum ht2)))
;(println (str "maximum: " (maximum ht2)))
;(println (str "search for: 5" (search hashtree 5)))
;
;;; ----------------------------------------------
;;; Tree Node: Tests
;;; ----------------------------------------------
;
;(deftest test_basic_hashtree
;  (is (= 4 (:key (minimum hashtree))))
;  (is (= 50 (:key (maximum hashtree)))))

;; ----------------------------------------------
;; Run all tests
;; ----------------------------------------------
;(run-tests 'algorithms.core)


;; TODO: extend this protocol to the existing HashTree impl
;"walk the tree using '[in|post|pre] order' traversal, and invoke the visitor function 'vf' on each node."
;(defrecord HashTreeGrapher
;
;  TRAVERSAL
;
;  (inorder [this tree vf]
;    (loop [stack [1]
;           visited #{}]
;      (let [ci (first stack)
;            li (left_child ci)
;            ri (right_child ci)]
;
;        (if (= 0 (count stack))
;          tree
;          (recur
;            (cond
;              (not (nil? (:table tree) li) && (not (contains visited li))) (list (cons li stack) visited)
;              (not (contains visited ci)) (do (vf (:table tree) ci) (list stack (assoc visited ci)))
;              (not (nil? (:table tree) ri) && (not (contains visited ri))) (list (cons ri stack) visited)
;              :else (list (rest stack) visited)))))))
;
;  (preorder [this tree vf]
;    (loop [stack [1]
;           visited #{}]
;      (let [ci (first stack)
;            li (left_child ci)
;            ri (right_child ci)]
;
;        (if (= 0 (count stack))
;          tree
;          (recur
;            (cond
;              (not (contains visited ci)) (do (vf (:table tree) ci) (list stack (assoc visited ci)))
;              (not (nil? (:table tree) li) && (not (contains visited li))) (list (cons li stack) visited)
;              (not (nil? (:table tree) ri) && (not (contains visited ri))) (list (cons ri stack) visited)
;              :else (list (rest stack) visited)))))))
;
;  (postorder [this tree vf]
;    (loop [stack [1]
;           visited #{}]
;      (let [ci (first stack)
;            li (left_child ci)
;            ri (right_child ci)]
;
;        (if (= 0 (count stack))
;          tree
;          (recur
;            (cond
;              (not (nil? (:table tree) li) && (not (contains visited li))) (list (cons li stack) visited)
;              (not (nil? (:table tree) ri) && (not (contains visited ri))) (list (cons ri stack) visited)
;              (not (contains visited ci)) (do (vf (:table tree) ci) (list stack (assoc visited ci)))
;              :else (list (rest stack) visited)))))))
;  )


