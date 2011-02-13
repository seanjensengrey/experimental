(ns algorithms.bst.testbst
  (:use [algorithms.bst.hashtree] :reload)
  (:use [algorithms.bst.treenode] :reload)
  (:use [algorithms.bst.testhelper] )
  (:use [algorithms.bst.protocols] )
  (:use [clojure.test]))

;; create test data
(def test-data (list 10 5 20 7 4 40 50))

;; ----------------------------------------------
;; TreeNode: Tests
;; ----------------------------------------------

;; create tree node to test
(def test-tree-node
  (loop [t (create-TreeNode nil nil 50)
         data test-data]
    (if (= 0 (count data))
      t
      (recur
        (insert t (create-TreeNode nil nil (first data)))
        (rest data)))))

;;; ----------------------------------------------
;;; Helper
;;; ----------------------------------------------

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

(deftest test_basic_tree
  (is (= 4 (:key (minimum test-tree-node))))
  (is (= 50 (:key (maximum test-tree-node)))))


;; ----------------------------------------------
;; HashTreeNode: Tests
;; ----------------------------------------------

(def test-hash-tree
  (loop [tht (dbg (create-HashTree))
         data (dbg test-data)]
    (if (= 0 (count data))
      (dbg tht)
      (recur
        (dbg (insert tht (create-Node (first data)(first data))))
        (dbg (rest data))))))

(visual-tree-test test-hash-tree "HashTree")

(deftest test_basic_hashtree
  (is (= 4 (:key (minimum test-hash-tree))))
  (is (= 50 (:key (maximum test-hash-tree)))))

;; ----------------------------------------------
;; Run all tests
;; ----------------------------------------------
(do
  (ns-publics 'algorithms.bst.testbst)
  (ns-interns 'algorithms.bst.testbst)
  (run-tests 'algorithms.bst.testbst))
