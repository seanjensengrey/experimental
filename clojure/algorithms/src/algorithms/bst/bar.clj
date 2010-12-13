(ns algorithms.bst.bar
  (:use [algorithms.bst.foo] :reload))

(defn test-persons-mouth [p food]
  (eat p food))

(test-persons-mouth (make-person) "ice-cream")

