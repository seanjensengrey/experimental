(ns algorithms.bst.protocols)


;; ----------------------------------------------
;; Debugging macros
;; ----------------------------------------------
;; uncomment this macro to turn on logging
;(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))

;; comment out this macro and uncomment the one above to turn on logging
(defmacro dbg[x] `(let [x# ~x] x#))

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

