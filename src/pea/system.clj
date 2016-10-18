(ns pea.system
  (:require
   [clojure.java.io :as io]
   [clojure.test :refer :all]
   [clojure.tools.logging :as log]
   [com.stuartsierra.component :as component]
   [environ.core :as env]
   [pea.components
    [config :as cfg]
    [bot :refer [new-bot]]]
   [system.components
    [datomic :refer [new-datomic-db]]
    [repl-server :refer [new-repl-server]]]
   ))

;;; global system variable
(def system  nil)




;; for tests - dbsystem
(defn new-dbsystem
  "System constructor.
  @return a new system map."
  []
  (let [config (cfg/new-config "test/config.edn")
        db_uri (cfg/get-db-uri config)]
    (component/system-map
     :db (new-datomic-db db_uri)
     :config config)))

(defn start-dbsystem
  []
  (component/start-system (new-dbsystem)))

(defn stop-dbsystem
  [system]
  (component/stop-system system))


;;; System constructor

(defn new-system
  "System constructor.
  @return a new system map."
  [config]
  (component/system-map
   :config config
   :db (new-datomic-db (cfg/get-db-uri config))
   :bot (component/using
         (new-bot config)
         [:config :db])))



(defn init!
  "Initialize the system."
  [config]
  (alter-var-root #'system (constantly (new-system config))))

(defn start!
  "Start the system."
  []
  (alter-var-root #'system component/start-system))

(defn stop!
  "Shuts down and destroys the current system."
  []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop-system s)))))
