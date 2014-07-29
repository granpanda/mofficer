(ns mofficer.main
  (:use [ring.middleware.json]
        [compojure.core])
  (:gen-class)
  (:require [ring.middleware.cors :as cors]
            [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [mofficer.persistence.user-config-dao :as user-config-dao]
            [mofficer.service.user-config-resource :as user-config-resource]))

(def response-headers-with-cors 
  {"Access-Control-Allow-Headers" "X-Requested-With,Content-Type,Accept,Origin,Authorization"
   "Access-Control-Allow-Methods" "OPTIONS,HEAD,GET,POST,PUT,DELETE"
   "Access-Control-Allow-Origin" "*"})

(defn http-options-filter [app request]
  (let [http-verb (name (:request-method request))]
    (if (.equalsIgnoreCase http-verb "OPTIONS") 
      { :status 204 :headers response-headers-with-cors } 
      (app request))))

(defn http-options-filter-middleware [app]
  (fn [request] (http-options-filter app request)))

(defn log-request [app request]
  (let [http-verb (name (:request-method request)) uri (:uri request)]
    (println "http-verb:" http-verb "-> uri:" uri)
    (app request)))

(defn simple-loggin-middleware [app]
  (fn [request] (log-request app request)))

(defn set-up-application [] (user-config-dao/create-user-configs-table-if-not-exists))

(defroutes api-routes
  (context "/mofficer/api" [] user-config-resource/user-config-api)
  (route/not-found "Not Found"))

(def app 
  (-> (handler/api api-routes)
      (cors/wrap-cors
        :access-control-allow-origin #".+" ; Java regex to match any char one or more times.
        :access-control-allow-headers ["Content-Type, Accept, Authorization"]
        :access-control-allow-methods ["OPTIONS, HEAD, GET, POST, PUT, DELETE"])
      (wrap-json-body {:keywords? true})
      (wrap-json-response {:pretty true})
      (http-options-filter-middleware)
      (simple-loggin-middleware)))

(defn start-server [] 
  (jetty/run-jetty app { :port 9022 :join? false }))

(defn -main [& args] 
  (set-up-application)
  (start-server))
