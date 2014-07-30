(ns mofficer.main
  (:use [ring.middleware.json]
        [compojure.core])
  (:gen-class)
  (:require [ring.middleware.cors :as cors]
            [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [mofficer.infrastructure.middlewares.logging-middleware :as logging-mid]
            [mofficer.infrastructure.middlewares.http-options-middleware :as http-options-mid]
            [mofficer.infrastructure.middlewares.authorization-middleware :as authorization-mid]
            [mofficer.persistence.user-config-dao :as user-config-dao]
            [mofficer.domain.workers.email-worker :as email-worker]
            [mofficer.service.email-resource :as email-resource]
            [mofficer.service.user-config-resource :as user-config-resource]))

(defn set-up-application [] 
  (user-config-dao/create-user-configs-table-if-not-exists)
  (email-worker/initialize-email-workers email-worker/number-of-workers email-worker/email-queue-name email-worker/send-message-from-queue-by-email))

(defroutes api-routes
  (context "/mofficer/api" [] user-config-resource/user-config-api email-resource/email-api)
  (route/not-found "Not Found"))

(def app 
  (-> (handler/api api-routes)
      (cors/wrap-cors
        :access-control-allow-origin #".+" ; Java regex to match any char one or more times.
        :access-control-allow-headers ["Content-Type, Accept, Authorization"]
        :access-control-allow-methods ["OPTIONS, HEAD, GET, POST, PUT, DELETE"])
      (wrap-json-body {:keywords? true})
      (wrap-json-response {:pretty true})
      (authorization-mid/authorize-request-middleware)
      (http-options-mid/http-options-filter-middleware)
      (logging-mid/simple-loggin-middleware)))

(defn start-server [] 
  (jetty/run-jetty app {:port 9022 :join? false}))

(defn -main [& args] 
  (set-up-application)
  (start-server))
