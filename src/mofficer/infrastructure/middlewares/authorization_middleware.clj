(ns mofficer.infrastructure.middlewares.authorization-middleware
  (:require [clj-http.client :as clj-http]
            [cheshire.core :as ches]
            [mofficer.infrastructure.utils.http-utils :as http-utils]))

(def autheo-url "http://localhost:9002/api/auth")

(defn get-autheo-ticket-from-request [request]
  (let [headers-map (ches/parse-string (ches/generate-string (:headers request)) true)
        auth-token (:authorization headers-map)
        http-verb (name (:request-method request))]
    {:tokenValue auth-token :httpVerb http-verb :requestedUrl (:uri request)}))

(defn get-put-request-with-body-as-json [body]
  (clj-http/put autheo-url {:socket-timeout 1000 :conn-timeout 1000 ; In milliseconds
                            :content-type :json :accept :json
                            :body (ches/generate-string body)
                            :throw-exceptions false}))

(defn get-autheo-authorization-response-http-status-code [autheo-ticket]
  (let [autheo-http-reponse (get-put-request-with-body-as-json autheo-ticket)]
    (:status autheo-http-reponse)))

(defn authorize-request-filter [app request]
  (let [autheo-ticket (get-autheo-ticket-from-request request)
        http-status-code (get-autheo-authorization-response-http-status-code autheo-ticket)]
    (if-not (= http-status-code 200)
      {:status http-status-code :headers http-utils/response-headers-with-cors}
      (app request))))

(defn authorize-request-middleware [app]
  (fn [request] (authorize-request-filter app request)))
