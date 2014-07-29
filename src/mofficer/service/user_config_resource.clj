(ns mofficer.service.user-config-resource
  (:use compojure.core)
  (:require [cheshire.core :as ches]
            [mofficer.domain.business.user-config-business :as user-config-business]))

(defroutes user-config-api
    (context "/user-configs" []
             (POST "/" { body :body } (user-config-business/create-user-config body))
             (GET "/:sender-username" [sender-username] (user-config-business/get-user-config sender-username))))
