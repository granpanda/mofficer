(ns mofficer.service.email-resource
  (:use [compojure.core])
  (:require [ring.util.response :as ring-resp]
            [mofficer.domain.business.email-business :as email-business]
            [mofficer.infrastructure.translators.email-info-translator :as email-info-translator]))

(defn send-email [sender-id email-info-map]
  (let [email-info (email-info-translator/get-email-info-from-map email-info-map)
        send-either (email-business/send-email sender-id email-info)]
    (if (nil? (:errorMessage send-either))
      {:status 202 :body send-either}
      {:status 500 :body send-either})))

(defroutes email-api
  (context "/emails/:sender-id" [sender-id]
           (POST "/" {email-info-map :body} (send-email sender-id email-info-map))))
