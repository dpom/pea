(ns user
  "Tools for interactive development with the REPL. This file should
    not be included in a production build of the application."
  (:require
   [clojure.test :refer :all]
   [environ.core :as env]
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [clojure.tools.logging :as log] 
   [clojure.tools.reader.edn :as edn]
   [clojure.java.classpath :as cp]
   [clojure.tools.trace :as trace]
   [pea.system :as sys]
   [pea.init :as core]
   [pea.components.config :as cfg])
  (:import (org.apache.log4j Level
                             Logger
                             PropertyConfigurator))
  )


(defn stop
  "Shuts down and destroys the current development system."
  []
  (core/stop))

(defn go
  "Initializes the current development system and starts it running."
  []
  (core/start))

(defn reset []
  (stop)
  (refresh :after 'user/go))

(defn reload-log!
  "Reconfigures log4j from a log4j.properties file on the classpath"
  []
  (with-open [config-stream
              (-> "log/log4j.properties"
                  ;; io/resource
                  io/file
                  io/input-stream)]
    (PropertyConfigurator/configure config-stream)))
