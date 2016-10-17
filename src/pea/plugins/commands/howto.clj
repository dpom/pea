(ns pea.plugins.commands.howto
  (:require
   [yetibot.core.hooks :refer [cmd-hook]]
   [clojure.string :refer [split]]
   ))

(defn howto-cmd
  "howto <topic> <key> # search in <topic> howtos <key> and display these howtos"
  {:yb/cat #{:util}}
  [{:keys [args]}]
  (let [[topic key] (split args)]
    (format "topic = %s, key = %s." topic key)))

(cmd-hook #"howto"
          _ howto-cmd)
