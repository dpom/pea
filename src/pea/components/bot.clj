(ns pea.components.bot
  (:require
   [clojure.tools.logging :as log]
   [com.stuartsierra.component :as component]
   [yetibot.core.logo :refer [logo]]
   [clojure.tools.nrepl.server :refer [start-server stop-server]]
   [yetibot.core.db :as db]
   [yetibot.core.adapters.init :as ai]
   [yetibot.core.logging :as logging]
   [yetibot.core.loader :refer [load-commands-and-observers]]
   [clojure.test :refer :all]
   [pea.handler :as web]
   )
)

(defrecord Bot [id config db]
  component/Lifecycle

  (start [this]
    (log/debugf "Start the %s component." id)
    (println logo)
    (start-server :port 6789)
    (web/start-web-server)
    (db/start)
    (ai/start)
    (logging/start)
    (load-commands-and-observers))

  (stop [this]
    (log/debugf "Stop the %s component." id)
    (shutdown-agents))
  )

(defn new-bot
  [config]
  (let [id :bot]
    (log/debugf "Create the %s component." id)
    (map->Bot {:id id :config config})))
