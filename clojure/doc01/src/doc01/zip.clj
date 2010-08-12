(ns doc01.zip
  (:import (java.util.zip ZipEntry ZipInputStream)
    (java.io File FileInputStream FileOutputStream BufferedInputStream BufferedOutputStream BufferedReader FileReader))
  (:require clojure.contrib.string))

;--------------------------------------------------------
;; references
;--------------------------------------------------------
;; http://java.sun.com/developer/technicalArticles/Programming/compression/
;; http://lethain.com/entry/2009/nov/15/reading-file-in-clojure/


;--------------------------------------------------------
; vars
;--------------------------------------------------------
(def BUFFER 2048)
(def FILE_NAME_SENTINEL "_file_#")
(def ENTRY_NAME_SENTINEL "_entry_#")

;--------------------------------------------------------
; file name encoding / decoding
;--------------------------------------------------------
(defn encoded? [#^File file]
  (.contains (.getName file) FILE_NAME_SENTINEL))

(defn encode-file-name [#^File zipfile #^String current-entry]
  (str FILE_NAME_SENTINEL (.getName zipfile) ENTRY_NAME_SENTINEL current-entry))

(defn decode-file-name [#^File file]
  (try
    (let [filename (.getName file)]
      (if (.contains filename FILE_NAME_SENTINEL)
        (let [zipfile (.substring filename (+ (.length FILE_NAME_SENTINEL) (.indexOf filename FILE_NAME_SENTINEL)) (.lastIndexOf filename ENTRY_NAME_SENTINEL))
              entry (.substring filename (+ (.length ENTRY_NAME_SENTINEL) (.lastIndexOf filename ENTRY_NAME_SENTINEL)))]
          {:zipfile zipfile :entry entry})
        nil))
    (catch Exception _ nil)))



;--------------------------------------------------------
; Zip file handling
;--------------------------------------------------------
(defn get-filelist-from-zipfile [#^File zipfile]
  "
  Get the file list from a zip file. This does *not* read the contents of the
  files, it only gets the manifest from the zip file:

    user=> (import 'java.io.File)
    java.io.File
    user=> (get-filelist-from-zipfile (File. \"./test/resources/a.zip\"))
    [\"b.txt\" \"c.zip\" \"d.txt\"]
  "
  (let [path (.getAbsolutePath zipfile)
        zis (ZipInputStream.
      (BufferedInputStream.
        (FileInputStream. zipfile)))]
    (loop [zipfiles []
           current-entry (.getNextEntry zis)]
      (if (= current-entry nil)
        zipfiles
        (recur
          (conj zipfiles (.getName current-entry))
          (.getNextEntry zis))))))

(defn extract-file [zipfile current-entry zis]
  "
  Extract a single file from a zipfile, create a tempfile, write the bytes from the zipfile into the tempfile,
  and return the tempfile.
  "
  (let [buf (make-array (. Byte TYPE) BUFFER)
        output-file (File/createTempFile "zip_" (encode-file-name zipfile current-entry))]
    (with-open [#^BufferedOutputStream bos (BufferedOutputStream. (FileOutputStream. output-file) BUFFER)]
      (do
        (println "processing zipfile: " zipfile ", entry: " current-entry ", tempfile: " (.getName output-file))
        (loop [bytes-read (.read zis buf 0 BUFFER)]
          (if (= bytes-read -1)
            output-file
            (recur
              (do
                (.write bos buf 0 bytes-read)
                (.read zis buf 0 BUFFER)))))))))

(defn extract-file-from-zipfile [#^String entry-name #^File zipfile]
  "
  Extract a single file from a zipfile. The extracted file is written to a
  temp-file, and this temp file is returned by the method.
  "
  (let [path (.getAbsolutePath zipfile)
        zis (ZipInputStream. (BufferedInputStream. (FileInputStream. zipfile)))]
    (loop [current-entry (.getNextEntry zis)]
      (do
        (if (nil? current-entry) nil)
        (if (.equals (.getName current-entry) entry-name)
          (extract-file zipfile current-entry zis)
          (recur (.getNextEntry zis)))))))

(defn extract-allfiles-from-zipfile [#^File zipfile]
  "
  Extract all the files from a zipfile (not recursive). The extracted files are written to
  temp-files, and these temp files are returned by the method. The temp file names are
  encoded as: [temp dir]/[temp name]FILE_NAME_SENTINEL[original file name]ENTRY_NAME_SENTINEL[entry name].

  As an example, if a file ./foo.zip contains bar.txt, then the resulting temp file would be named:

    /tmp/$$$$$$_file_#@$_foo.zip_entry_$%@_bar.txt

  "
  (let [path (.getAbsolutePath zipfile)
        zis (ZipInputStream. (BufferedInputStream. (FileInputStream. zipfile)))]
    (loop [file-list []
           current-entry (.getNextEntry zis)]
      (if (nil? current-entry)
        file-list
        (recur (conj file-list (extract-file zipfile current-entry zis)) (.getNextEntry zis))))))

