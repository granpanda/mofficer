(ns mofficer.service.email-resource
  (:use [compojure.core])
  (:require [ring.util.response :as ring-resp]
            [mofficer.infrastructure.datastructures.either]
            [mofficer.domain.business.email-business :as email-business]
            [mofficer.infrastructure.translators.email-info-translator :as email-info-translator])
  (:import [mofficer.infrastructure.datastructures.either Either]))

(defn send-email [sender-id email-info-map]
  (let [email-info (email-info-translator/get-email-info-from-map email-info-map)]
    (if-not (nil? (:senderEmail email-info))
      (do (future (email-business/send-email sender-id email-info))
          {:status 202})
      {:status 500})))

(defroutes email-api
  (context "/emails/:sender-id" [sender-id]
           (POST "/" { email-info-map :body } (send-email sender-id email-info-map))))
