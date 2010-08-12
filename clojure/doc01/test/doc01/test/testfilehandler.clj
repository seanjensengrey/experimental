(ns doc01.test.testfilehandler
  (:use [doc01.filehandler] :reload-all)
  (:use [doc01.test.testhelper] :reload-all)
  (:use [doc01.test.testzip] :reload-all)
  (:require [doc01.zip :as zip])
  (:use [clojure.test])
  (:use [clojure.set])
  (:import (java.io File)))

(alias 'fh 'doc01.filehandler)

(deftest test-get-file-type-string
  (is (= ::fh/TEXT (get-file-type "a.txt")))
  (is (= ::fh/ZIP (get-file-type "b.zip")))
  (is (= ::fh/UNKNOWN (get-file-type "b.foobar"))))

(deftest test-get-file-type-file
  (is (= ::fh/TEXT (get-file-type (File. "a.txt"))))
  (is (= ::fh/ZIP (get-file-type (File. "b.zip"))))
  (is (= ::fh/UNKNOWN (get-file-type (File. "b.foobar")))))

(deftest test-handle-filetypes-text
  "
  Expect to receive a single file, 'foo.txt'.
  "
  (let [input-file (File. "./test/resources/text/foo.txt")
        expected #{"foo.txt"}
        result (handle-file-types input-file)]
    (is (.endsWith "foo.txt" (.getName result)))))

(deftest test-handle-filetypes-zipfile
  "
  Expect to receive a sequence (lazy) with two files.
  "
  (let [input-zip (File. "./test/resources/mixed/a.zip")
        expected #{"b.txt" "d.txt"}
        handled-file-types-result (handle-file-types input-zip)
        handled-file-names (map #(:entry (zip/decode-file-name %)) handled-file-types-result)
        ]
      (is (subset? expected (set handled-file-names)))))

