(ns pea.models.study
  (:require
   [clojure.tools.logging :as log]
   [datomic.api :as d]
   [pea.system :as sys]
   [clojure.test :refer :all]
   ))

;; for tests
(use-fixtures :once
  (fn [tests]
    (println "============== start models study tests")
    (sys/start-dbsystem!)
    (tests)
    (sys/stop!)
    (println "============== stop models study tests")
    ))


;; main functions

(defn get-res-type
  "Get response type.
  @param bid - response bid
  @param q - response value (1 or 2)
  @return the response type (true, false or neutral)"
  [bid q]
  (let [db (d/db (sys/get-db-conn))]
  (get (first (seq (d/q '[:find ?rt1 ?rt2
         :in $ ?bid 
         :where
         [?c :study/bid ?bid]
         [?c :study/res1type ?rt1]
         [?c :study/res2type ?rt2]]
       db
       bid)))
       (dec q)
       "neutral")))

(deftest get-res-type-test
  (is (= "false" (get-res-type "hist101" 1)) "first res")
  (is (= "true" (get-res-type "hist101" 2)) "second res")
  (is (= "neutral" (get-res-type "hist999" 1)) "neutral res")
  (is (= "neutral" (get-res-type "cucu" 1)) "wrong bid")
  )


(defn get-res-next
  "Get next item from response.
  @param bid - response bid
  @param q - response value (1 or 2)
  @return the next item"
  [bid q]
  (let [db (d/db (sys/get-db-conn))]
    (get (first (seq (d/q '[:find ?rn1 ?rn2
                            :in $ ?bid 
                            :where
                            [?c :study/bid ?bid]
                            [?c :study/res1next ?rn1]
                            [?c :study/res2next ?rn2]]
                          db
                          bid)))
         (dec q))))

(deftest get-res-next-test
  (is (= "hist102" (get-res-next "hist101" 1)) "first res")
  (is (= "hist103" (get-res-next "hist101" 2)) "second res")
  )




(defn get-study-by-bid
  "Get study from database using his bid.
  @param bid - study's bid
  @return the study map"
  [bid]
  (let [db (d/db (sys/get-db-conn))]
    (ffirst (d/q '[:find (pull ?c [:study/bid :study/title :study/text])
                   :in $  ?bid
                   :where
                   [?c :study/bid ?bid]]
                 db
                 bid))))


(deftest get-study-by-bid-test
  (is (= {:study/bid "hist999",
          :study/text
          "Imi pare rău. Această versiune demo nu conţine alte item-uri."}
         (get-study-by-bid "hist999"))
      "good bid")
  (is (nil? (get-study-by-bid "linux"))
      "nok bid")
  )




