(ns doc01.test.testmain
  (:use [doc01.main] :reload-all)
  (:use [clojure.set])
  (:use [clojure.test])
  )

(defn check-data [entry]
  (is (.equals (:title entry) "Apocolocyntosis"))
  (is (.equals (:author entry) "Lucius Seneca"))
  (is (.equals (:release-date entry) "November 10, 2003 [EBook #10001]"))
  (is (.equals (:last-updated entry) "April 9, 2005"))
  (is (.equals (:language entry) "English")))

(deftest test-get-entries
  (let [entries (get-entries "./test/resources/gutenberg")]
    (map check-data entries)))
