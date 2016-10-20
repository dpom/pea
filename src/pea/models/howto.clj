(ns pea.models.howto
  (:require
   [clojure.tools.logging :as log]
   [datomic.api :as d]
   [pea.system :as sys]
   [clojure.test :refer :all]
   ))

;; for tests
(use-fixtures :once
  (fn [tests]
    (println "============== start models howto tests")
    (sys/start-dbsystem!)
    (tests)
    (sys/stop!)
    (println "============== stop models howto tests")
    ))


;; main function
(defn get-howto
  "Get howtos from database using maximum 3 words.
  @param w1 -  first word
  @param w2 -  second word (optional)
  @param w3 -  third word (optional)
  @return a vector with all entities that match all 3 words"
  ([w1] (get-howto w1 w1 w1))
  ([w1 w2] (get-howto w1 w2 w2))
  ([w1 w2 w3]
   (let [db (d/db (sys/get-db-conn))
         rules '[[[searchtt ?c ?w1 ?w2 ?w3]
                  [(fulltext $ :howto/title ?w1) [[?c ?t]]]
                  [(fulltext $ :howto/title ?w2) [[?c ?t]]]
                  [(fulltext $ :howto/title ?w3) [[?c ?t]]]]
                 [[searchtt ?c ?w1 ?w2 ?w3]
                  [(fulltext $ :howto/text ?w1) [[?c ?t]]]
                  [(fulltext $ :howto/text ?w2) [[?c ?t]]]
                  [(fulltext $ :howto/text ?w3) [[?c ?t]]]]
                 ]]
     (flatten (d/q '[:find (pull ?c [:howto/title :howto/text])
                     :in $ % ?w1 ?w2 ?w3
                     :where
                     [?c :howto/title ?title]
                     [?c :howto/text ?text]
                     [searchtt ?c ?w1 ?w2 ?w3]]
                   db
                   rules
                   w1 w2 w3)))))

(deftest get-howto-test
  (let [r1 (get-howto "linux" "bigest" "find")
        r2 (get-howto "clojure" "destructuring" "maps")
        r3 (get-howto "linux")
        title (partial mapv :howto/title)]
    (is (= [{:howto/title "(Linux) Find The Bigest Dirs",
             :howto/text
             "On linux shell:\ndu -Sk | sort -nr | head -n10 "}]
           r1)
        "linux bigest find")
    (is (= ["(Clojure) Associative Destructuring"]
           (title r2))
        "clojure destruct map")
    (is (= ["(Linux) Find The Bigest Dirs"
            "(Linux) Limit the cpu usage of a process."]
           (title r3))
        "single word")
    ))
