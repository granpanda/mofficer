(ns mofficer.main
  (:use [ring.middleware.json]
        [compojure.core])
  (:gen-class)
  (:require [ring.middleware.cors :as cors]
            [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-http.client :as http-client]
            [cheshire.core :as ches]
            [mofficer.persistence.user-config-dao :as user-config-dao]
            [mofficer.domain.workers.email-worker :as email-worker]
            [mofficer.service.email-resource :as email-resource]
            [mofficer.service.user-config-resource :as user-config-resource]))

(def response-headers-with-cors 
  {"Access-Control-Allow-Headers" "X-Requested-With,Content-Type,Accept,Origin,Authorization"
   "Access-Control-Allow-Methods" "OPTIONS,HEAD,GET,POST,PUT,DELETE"
   "Access-Control-Allow-Origin" "*"})

(def autheo-url "http://localhost:9002/autheo/api/auth")

(defn get-autheo-ticket-from-request [request]
  (let [auth-token (:Authorization (:headers request))
        http-verb (name (:request-method request))]
    {:tokenValue auth-token :httpVerb http-verb :requestedUrl (:uri request)}))

(defn get-put-request-with-body-as-json [body]
  (http-client/put autheo-url { :socket-timeout 1000 :conn-timeout 1000 ; In milliseconds
                                :content-type :json :accept :json
                                :body (ches/generate-string body)
                                :throw-exceptions false }))

(defn get-autheo-authorization-response-http-status-code [autheo-ticket]
  (let [autheo-http-reponse (get-put-request-with-body-as-json autheo-ticket)]
    (:status autheo-http-reponse)))

(defn authorize-request-filter [app request]
  (let [autheo-ticket (get-autheo-ticket-from-request request)
        http-status-code (get-autheo-authorization-response-http-status-code autheo-ticket)]
    (if-not (= http-status-code 200)
      {:status http-status-code :headers response-headers-with-cors}
      (app request))))

(defn authorize-request-middleware [app]
  (fn [request] (authorize-request-filter app request)))
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
      (http-options-filter-middleware)
      (simple-loggin-middleware)))

(defn start-server [] 
  (jetty/run-jetty app { :port 9022 :join? false }))

(defn -main [& args] 
  (set-up-application)
  (start-server))
