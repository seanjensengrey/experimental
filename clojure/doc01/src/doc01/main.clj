(ns doc01.main
  (:import
    (java.io File)
    (java.io BufferedReader FileReader))
  (:require
    [clojure.contrib.string]
    [clojure.contrib.repl-utils]
    [doc01.filehandler :as fh]
    [doc01.zip :as zip]))

(alias 'fh 'doc01.filehandler)

;--------------------------------------------------------
;GutenbergEntry -- contains all the metadata
;--------------------------------------------------------
(defrecord GutenbergEntry [filename decoded-zipfile decoded-filename title author release-date last-updated language])
(defn create-gutenberg-entry [file]
  (let [ actual-filename (.getAbsolutePath file)
         temp-filename (zip/decode-file-name file)
         decoded-zipfile (:zipfile temp-filename "UNKNOWN") 
         decoded-filename (:entry temp-filename "UNKNOWN")]
    (cond
      (= ::fh/TEXT (fh/get-file-type file))
      (let [
        ;; TODO: no need to read entire file...only the first 50 lines or so
        ;; TODO: only works on text files...need to make this some type of handler so that .text and .html files are processed differently
        text (slurp file)
        title         (or (last (re-find #"(Title: )(.*)" text)) "UNKNOWN")
        author        (or (last (re-find #"(Author: )(.*)" text)) "UNKNOWN")
        release-date  (or (last (re-find #"(Release Date: )(.*)" text)) "UNKNOWN")
        last-updated  (or (first (take-last 2 (re-find #"(?i)(Last updated: )(.*)(])" text))) "UNKNOWN")
        language      (or (last (re-find #"(Language: )(.*)" text)) "UNKNOWN")
        entry (GutenbergEntry. actual-filename decoded-zipfile decoded-filename title author release-date last-updated language)]
        entry)
      (= ::fh/ZIP (fh/get-file-type file))
      (println "todo"))))



(defn get-entries [dir]
  (map #(create-gutenberg-entry %) (fh/get-handled-files dir)))


