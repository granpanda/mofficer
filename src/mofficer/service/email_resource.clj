(ns mofficer.service.email-resource
  (:use [compojure.core])
  (:require [ring.util.response :as ring-resp]
            [mofficer.infrastructure.datastructures.either]
            [mofficer.domain.business.email-business :as email-business]
            [mofficer.domain.business.user-config-business :as user-config-business])
  (:import [mofficer.infrastructure.datastructures.either Either]))

(defn- queue-email-request [sender-id email-info]
  (let [sender-email (:senderEmail email-info)
        user-config (user-config-business/get-user-config-by-email sender-email)]
    (if (= sender-id (:senderId user-config))
      (email-business/add-message-to-email-queue user-config email-info)
      (Either. "The user who is calling the service and the email owner are not the same" nil))))

(defroutes email-api
  (context "/emails/:sender-id" [sender-id]
           (POST "/" { email-info :body } (let [queue-email-response (queue-email-request sender-id email-info)]
                                            (if-not (nil? (:successAnswer queue-email-response))
                                              {:status 202 :body (:successAnswer queue-email-response)}
                                              {:status 500 :body (:errorMessage queue-email-response)})))))
