(ns mofficer.service.email-resource
  (:use [compojure.core])
  (:require [ring.util.response :as ring-resp]
            [mofficer.domain.business.email-business :as email-business]
            [mofficer.domain.business.user-config-business :as user-config-business]))

(defn- queue-email-request [email-info]
  (let [user-config (user-config-business/get-user-config-by-email (:senderEmail email-info))]
    (email-business/queue-email-request user-config email-info)))

(defroutes email-api
  (context "/emails" []
           (POST "/" { email-info :body } {:status 202
                                           :body (queue-email-request email-info)})))
