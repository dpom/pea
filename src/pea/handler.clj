(ns pea.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [org.httpkit.server :refer [run-server]]
            [yetibot.core.webapp.routes.home :refer [home-routes]]
            [yetibot.core.webapp.routes.api :refer [api-routes]]
            [yetibot.core.webapp.middleware :as middleware]
            [yetibot.core.webapp.session :as session]
            [yetibot.core.webapp.route-loader :as rl]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [clojure.tools.logging :as log]
            [clojure.tools.nrepl.server :as nrepl]))

(defonce nrepl-server (atom nil))

(defonce web-server (atom nil))

(defroutes base-routes
  (route/resources "/")
  (route/not-found "Not Found"))


(defn init
  "init will be called once when app is deployed as a servlet on an app server
   such as Tomcat put any initialization code here"
  []
  ;; start the expired session cleanup job
  (session/start-cleanup-job!)
  (log/info "pea.webapp started successfully"))

(defn destroy
  "destroy will be called when your application shuts down. put any clean up
   code here"
  []
  (log/info "pea is shutting down...")
  (log/info "shutdown complete!"))

(defn app []
  (let [plugin-routes (vec (rl/load-plugin-routes))
        ; base-routes needs to be very last because it contains not-found
        last-routes (conj plugin-routes base-routes)]
    (-> (apply routes
               home-routes
               api-routes
               last-routes)
        middleware/wrap-base)))

;; base-routes

(defn start-web-server []
  (init)
  (reset! web-server (run-server (app) {:join? false :daemon? true :port 3000})))

(defn stop-web-server []
  (when-not (nil? @web-server)
    (@web-server :timeout 100)
    (reset! web-server nil)))
