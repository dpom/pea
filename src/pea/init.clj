(ns pea.init
  (:require
    [yetibot.core.chat :as chat]
    [yetibot.core.adapters.init :as ai]
    [clojure.stacktrace :refer [print-stack-trace]]
    [clojure.tools.nrepl.server :refer [start-server stop-server]]
    [yetibot.core.config :as config]
    [yetibot.core.db :as db]
    [taoensso.timbre :refer [info warn error]]
    [yetibot.core.logging :as logging]
    [pea.handler :as web]
    [yetibot.core.loader :refer [load-commands-and-observers]]
    [yetibot.core.handler :refer [handle-unparsed-expr]]
    [yetibot.core.logo :refer [logo]])
  (:gen-class))

(defn welcome-message []
  (println logo))

(defn -main [& args]
  ;; only continue if able to load config
  (if-let [c (config/reload-config!)]
    (do
      (welcome-message)
      (start-server :port 6789)
      (web/start-web-server)
      (db/start)
      (logging/start)
      (ai/start)
      (load-commands-and-observers))
    (do
      (error "PEA failed to start: please ensure config is in place at" config/config-path " and that it is well-formed (see the log above for details)")
      (shutdown-agents))))
