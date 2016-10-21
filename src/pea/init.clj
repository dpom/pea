(ns pea.init
  (:require
   [environ.core :as env]
   [pea.system :as sys]
   [pea.components.config :as cfg]
   [clojure.tools.logging :as log])
  (:gen-class))

(defn start
  "Start application."
  []
  (sys/init! (cfg/new-config (env/env :config-file)))
  (sys/start!))

(defn stop
  "Stop application."
  []
  (sys/stop!))

(defn -main
  [& args]
  (start))


