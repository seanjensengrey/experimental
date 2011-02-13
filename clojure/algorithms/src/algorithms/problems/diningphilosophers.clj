(ns algorithms.problems.diningphilosophers)

;; ----------------------------------------------
;; references
;; ----------------------------------------------
;; The Art of Multiprocessor Programming, Herlihy & Shavit, page. 16
;; http://onclojure.com/2009/03/04/dorun-doseq-doall/
;; http://thinkrelevance.com/blog/2008/09/18/pcl-clojure-chapter-7.html
;; http://clojure.org/concurrent_programming

;; ----------------------------------------------
;; helper fx for table printing
;; ----------------------------------------------

(defn print-table [table]
  (dosync
    (prn "-----------------------------------------------------------------------------------------------")
    (prn "Table: ")
    (prn "  state: " @(:tablestate table) " seats: " (:seats table))
    (pr "  chopsticks: ")(doseq [ch (:chopsticks table)] (pr (key ch) @(val ch) "  "))(prn)
    (pr "  servings: ")(doseq [sv (:servings table)] (pr (key sv) ":"  @(val sv) "  "))(prn)
    (prn "-----------------------------------------------------------------------------------------------")))

;; ----------------------------------------------
;; Methods for
;; * creating tables
;; * eating, thinking
;; * starting, stopping table service
;; ----------------------------------------------

(defn create-table [seats]
  "create and init a table with chopsticks, philosophers, and a serving count for each diner"
  (let [ch (zipmap (range seats) (map ref (take seats (repeat :table))))
        ph (zipmap (range seats) (map ref (take seats (repeat :thinking))))
        servings (zipmap (range seats) (map ref (take seats (repeat 0))))]
    {:seats seats :chopsticks ch :philosophers ph :tablestate (ref :dinnertime) :servings servings}))

(defn try-eating [table ph-index]
  "try to eat. the diner may not be able to, as they may already be eating, or they may not have
  access to the chopsticks to their left and right."
  (let [ph ((:philosophers table) ph-index)
        left-ch ((:chopsticks table) ph-index)
        right-ch ((:chopsticks table) (mod (+ 1 ph-index) (:seats table)))]
    (do
      (dosync
        (if (and (= :thinking @ph) (= :table @left-ch) (= :table @right-ch))
          (do
            (ref-set left-ch ph)
            (ref-set right-ch ph)
            (ref-set ph :eating)
            (alter ((:servings table) ph-index) + 1))))
      (Thread/sleep (rand-int 100)))))

(defn try-thinking [table ph-index]
  "try to think. if the philosopher is currently eating, then this will cause him/her to surrender their
  chopsticks and start thinking."
  (let [ph ((:philosophers table) ph-index)
        left-ch ((:chopsticks table) ph-index)
        right-ch ((:chopsticks table) (mod (+ 1 ph-index) (:seats table)))]
    (do
      (dosync
        (if (and (= :eating @ph) (= ph @left-ch) (= ph @right-ch))
          (do
            (ref-set left-ch :table)
            (ref-set right-ch :table)
            (ref-set ph :thinking))))
      (Thread/sleep (rand-int 100)))))

(defn start-dining [table ph-index]
  "start eating and thinking for as long as the table is served (e.g. it is dinnertime)"
  (while (= @(:tablestate table) :dinnertime)
    (try-eating table ph-index)
    (try-thinking table ph-index)))

(defn start-tableservice [table]
  "the table has been served with food."
  (doseq [seat (range (:seats table))]
    (future (start-dining table seat))))

(defn stop-tableservice [table]
  "food service is complete, and the diners must stop eating."
  (dosync (alter (:tablestate table) :closingtime)))

;; ----------------------------------------------
;; primitive test
;; ----------------------------------------------

(prn "creating table")
(def table-ref (ref (create-table 5)))
(print-table @table-ref)

(prn "starting table service")
(dosync (start-tableservice @table-ref))
(print-table @table-ref)

(prn "waiter is sleeping, while philosophers eat...")
(def loop_count
  (loop [i 0]
    (if (> i 5)
      i
      (recur (do (print-table @table-ref) (Thread/sleep 1000) (inc i))))))

(prn "stopping table service")
(stop-tableservice @table-ref)

;; TODO: replace hardcoded thread/sleep with maits on the chopstick refs
(prn "waiting for participants to complete eating...")
(Thread/sleep 500)
(print-table @table-ref)

;; ----------------------------------------------
;; Sample Run Output
;; ----------------------------------------------

;"creating table"
;"-----------------------------------------------------------------------------------------------"
;"Table: "
;"  state: " :dinnertime " seats: " 5
;"  chopsticks: "4 :table "  "3 :table "  "2 :table "  "1 :table "  "0 :table "  "
;"  servings: "4 ":" 0 "  "3 ":" 0 "  "2 ":" 0 "  "1 ":" 0 "  "0 ":" 0 "  "
;"-----------------------------------------------------------------------------------------------"
;"starting table service"
;"-----------------------------------------------------------------------------------------------"
;"Table: "
;"  state: " :dinnertime " seats: " 5
;"  chopsticks: "4 :table "  "3
;"-----------------------------------------------------------------------------------------------"
;"Table: "
;"  state: " :dinnertime " seats: " 5
;"  chopsticks: "4 :table "  "3 :table "  "2 :table "  "1 :table "  "0 :table "  "
;"  servings: "4 ":" 0 "  "3 ":" 0 "  "2 ":" 1 "  "
;<SNIP...>
;"Table: "
;"  state: " :dinnertime " seats: " 5
;"  chopsticks: "4 :table "  "3 :table "  "2 #<Ref@5675b3ee: :eating> "  "1 #<Ref@5675b3ee: :eating> "  "0 :table "  "
;"  servings: "4 ":" 26 "  "3 ":" 27 "  "2 ":" 29 "  "1 ":" 27 "  "0 ":" 28 "  "
;"-----------------------------------------------------------------------------------------------"
;"stopping table service"
;"waiting for participants to complete eating..."
;"-----------------------------------------------------------------------------------------------"
;"Table: "
;"  state: " nil " seats: " 5
;"  chopsticks: "4 :table "  "3 :table "  "2 :table "  "1 :table "  "0 :table "  "
;"  servings: "4 ":" 34 "  "3 ":" 35 "  "2 ":" 32 "  "1 ":" 38 "  "0 ":" 28 "  "
;"-----------------------------------------------------------------------------------------------"
;Disconnected from the target VM, address: '127.0.0.1:49513', transport: 'socket'
;
;Process finished with exit code 0

