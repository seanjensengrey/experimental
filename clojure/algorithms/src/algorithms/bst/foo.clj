(ns algorithms.bst.foo)

(defprotocol MOUTH
  (eat [this food]))

(defrecord Person []
  MOUTH
  (eat [this food]
    (println (str this " is chewing " food))))

(defn make-person [] (Person.))
