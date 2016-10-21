(ns pea.components.config
  (:require
   [clojure.tools.logging :as log]
   [clojure.edn :as edn]
   [clojure.test :refer :all]
   [environ.core :as env]
   [yetibot.core.config :as yet]
   ))


(defn- load-edn
  "Load and evaluate an edn file.
  @params path - the file path
  @return the evaluation or nil if file could not be read"
  [path]
  (try
    (edn/read-string (slurp path))
    (catch Exception e
      (log/error "Failed loading config: " e)
      nil)))

(defn new-config
  "Config constructor.
  @param config_file - configuration file name
  @return a new config map."
  [config_file]
  (yet/reload-config! config_file)
  (load-edn config_file))

(deftest new-config-test
  (is (= {:yetibot
          {:db {:datomic-url "datomic:mem://peatest"},
           :type :demo
           :adapters
           [{:name "freenode-irc",
             :type :irc,
             :host "chat.freenode.net",
             :port "6665",
             :username "pea_yetibot",
             :rooms #{"#pea" "#yetibot"}}]}}
         (new-config (env/env :config-file)))))


(defn get-db-uri
  "Get the datbase URI from config.
  @param config - configuration map
  @return the uri as string"
  [config]
  (get-in config [:yetibot :db :datomic-url]))

(deftest get-db-uri-test
  (is (= "datomic:mem://peatest"
         (get-db-uri (new-config (env/env :config-file))))))


(defn get-app-type
  "Get the application's type.
  @param config - configuration map
  @return application type (:demo or :prod)"
  [config]
  (get-in config [:yetibot :type] :prod))
