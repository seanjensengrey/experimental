(ns doc01.filehandler
  (:import (java.io File))
  (:require [doc01.zip :as zip]))

;--------------------------------------------------------
;file types
; ZIP | TEXT | UNKNOWN
;--------------------------------------------------------
(defmulti get-file-type class)
(defmethod get-file-type String [file-name]
  (let [fname (.toLowerCase file-name)
        lindex (.lastIndexOf fname ".")
        ending (.substring fname lindex)]
    (cond
      (.equals ending ".zip") ::ZIP
      (.equals ending ".jar") ::ZIP
      (.equals ending ".txt") ::TEXT
      (.equals ending ".text") ::TEXT
      :default ::UNKNOWN)))
(defmethod get-file-type File [file]
  (get-file-type (.getName file)))

;--------------------------------------------------------
;file handlers
;--------------------------------------------------------
(defn get-files [dir]
  (filter #(.isFile %) (file-seq (File. dir))))

(defn handle-file-types [input-file]
  (let [file-type (get-file-type input-file)]
    (if (= ::ZIP file-type)
      (let [zipped-files (zip/extract-allfiles-from-zipfile input-file)]
        (flatten (map handle-file-types zipped-files)))
      (if (= ::TEXT file-type)
        input-file
        (println "unknown file type, skipping: " input-file)))))

(defn get-handled-files [dir]
  (let [source-files (get-files dir)]
    (flatten (map handle-file-types source-files))))
