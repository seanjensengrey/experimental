(ns doc01.test.testhelper
  (:import (java.io File))
  (:use [clojure.contrib.repl-utils]))

(defn get-appended-filename [file]
  (.substring (.getName file) (+ 1 (.lastIndexOf (.getName file) "#"))))

(defn debug [item]
  (do
    (println "------------------------------------")
    (println "item: " item)
    (println "show:")
    (show item)
    (println "ancestors:")
    (ancestors item)
    (println "class:")
    (class item)
    (println "------------------------------------")))


