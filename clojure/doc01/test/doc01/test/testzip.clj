(ns doc01.test.testzip
  (:use [doc01.zip] :reload-all)
  (:use [doc01.test.testhelper])
  (:use [clojure.test])
  (:use [clojure.set])
  (:import (java.io File )))

(deftest get-filelist
  (let [input (File. "./test/resources/mixed/a.zip")
        expected #{"b.txt" "c.zip"}]
    (is (subset? expected (set (get-filelist-from-zipfile input))))))

(deftest get-file 
  (let [input-zip (File. "./test/resources/mixed/a.zip")
        expected-text (slurp "./test/resources/expected/b.txt")
        actual-output (extract-file-from-zipfile "b.txt" input-zip)
        actual-text (slurp (.getAbsolutePath actual-output))] 
    (do
      (is (.equals expected-text actual-text)))))


(deftest get-all-files
  (let [input-zip (File. "./test/resources/mixed/a.zip")
        expected-files #{"b.txt" "c.zip"}
        actual-files (extract-allfiles-from-zipfile input-zip)]
    (do
      (let [filtered-files (set (map get-appended-filename actual-files))]
        (is (subset? expected-files filtered-files))))))

(deftest is-not-encoded
  (is (not (encoded? (File. "abc"))))
  (is (not (encoded? (File. (str "/foo/bar/" ENTRY_NAME_SENTINEL)))))
  (is (not (encoded? (File. "/foo/bar/_zipfile_")))))

(deftest is-encoded
  (is (encoded? (File. FILE_NAME_SENTINEL)))
  (is (encoded? (File. (str "abc" FILE_NAME_SENTINEL))))
  (is (encoded? (File. (str "abc" FILE_NAME_SENTINEL "abc"))))
  (is (encoded? (File. (str "abc" FILE_NAME_SENTINEL ENTRY_NAME_SENTINEL)))))

(deftest encode-a-file-name
  (is (let [encoded-name (encode-file-name (File. "abc") "def.ghi")]
      (.endsWith encoded-name "ghi" ))))
