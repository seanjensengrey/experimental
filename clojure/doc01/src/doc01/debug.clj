(ns doc01.debug)

;------------------------------------------
; create a reader
;------------------------------------------
(defn readr [prompt exit-code]
  (let [input (clojure.main/repl-read prompt exit-code)]
    (if (= input ::tl) exit-code input)))

;------------------------------------------
; get the local context
;------------------------------------------
(defmacro local-context []
  (let [symbols (keys &env)]
    (zipmap (map (fn [sym] `(quote ~sym)) symbols) symbols)))

;------------------------------------------
; define a break point macro
;------------------------------------------
(defmacro break []
  `(clojure.main/repl
    :prompt #(print "debug=> ")
    :read readr :eval (partial contextual-eval (local-context))))

;;------------------------------------------
;; test
;;------------------------------------------
;(defn div [n d]
;  (break)
;  (int (/ n d)))
;
;(div 10 0)
