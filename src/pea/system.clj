(ns pea.system
  (:require
   [clojure.java.io :as io]
   [clojure.test :refer :all]
   [clojure.tools.logging :as log]
   [com.stuartsierra.component :as component]
   [environ.core :as env]
   [pea.components
    [config :as cfg]
    [db :refer [new-db]]
    [bot :refer [new-bot]]]
   ))

;;; global system variable
(def system  nil)



;;; System constructor

(defn new-system
  "System constructor.
  @return a new system map."
  [config]
  (component/system-map
   :config config
   :db (new-db config)
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

;; for tests - dbsystem

(defn new-dbsystem
  "System constructor.
  @return a new system map."
  []
  (let [config (cfg/new-config "test/config.edn")]
    (component/system-map
     :config config
     :db (new-db config))))

(defn start-dbsystem!
  []
  (alter-var-root #'system (constantly (new-dbsystem)))
  (start!))

;;; aux functions

(defn get-db-conn
  "Get database connection."
  []
  (get-in system [:db :conn]))
