(ns pea.components.db
  (:require
   [com.stuartsierra.component :as component]
   [datomic.api :as d]
   [clojure.java.io :as io]
   [clojure.tools.logging :as log]
   [pea.components [config :as cfg]]
   [clojure.test :refer :all]
   ))

;; global constants
(def demo_schema "db/schema.edn")
(def demo_seed_data "db/data.edn")

;; for tests
(def testuri "datomic:mem://peatest")
(def testconn (atom nil))


(defn set-schema!
  "Set a schema in the database.
  @param conn - database connection
  @param fname - name of the file containing the schema.
  @return schema"
  [conn fname]
  (let [sch (read-string (slurp fname))]
    @(d/transact conn sch)
    sch))

(deftest set-schema!-test
  (let [s (set-schema! @testconn "test/schema0.edn")]
    (is (= [:howto/bid
            :howto/title
            :howto/text
            :study/bid
            :study/text
            :study/res1next
            :study/res1type
            :study/res2next
            :study/res2type]
           (mapv :db/ident s)))
   ))

(defn load-seed-data!
  "Load seed data (for demos) in database.
  @param conn - database connection
  @param fname - name of the file containing the seed data
  @return seed data"
  [conn fname]
  (let [ds (read-string (slurp fname))]
    @(d/transact conn ds)
    ds))

(deftest load-seed-data!-test
  (let [sd (load-seed-data! @testconn "test/data0.edn" )
        res (d/q '[:find ?c :where [?c :howto/title]] (d/db @testconn))
        id (ffirst res)
        entity (-> @testconn
                   d/db
                   (d/entity id))]
    (is (= 5 (count res)) "count")
    (is (= [:howto/bid :howto/title :howto/text]
           (keys entity)) "keys")
    ))

(defn test-ns-hook []
  (println "============== start db test hook")
  (d/create-database testuri)
  (reset! testconn (d/connect testuri))

  (set-schema!-test)
  (load-seed-data!-test)

  (d/release @testconn)
  (d/delete-database testuri)
  (reset! testconn nil)
  (println "============== stop db test hook")
  )


;; Db component

(defrecord Db [id uri conn type]
  component/Lifecycle

  (start [component]
    (log/debugf "Start the %s component." id)
    (d/create-database uri)
    (let [conn (d/connect uri)]
      (when (= type :demo)
        (set-schema! conn (io/resource demo_schema))
        (load-seed-data! conn (io/resource demo_seed_data)))
      (assoc component :conn conn)))

  (stop [component]
    (log/debugf "Stop the %s component." id)
    (when conn
      (d/release conn)
      (when (= type :demo)
        (d/delete-database uri)))
    (assoc component :conn nil))
  )

(defn new-db [config]
  (let [id :db]
    (log/debugf "Create the %s component." id)
    (map->Db {:id id
              :uri (cfg/get-db-uri config)
              :type (cfg/get-app-type config)})))
