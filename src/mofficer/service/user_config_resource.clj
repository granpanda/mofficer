(ns mofficer.service.user-config-resource
  (:use [compojure.core])
  (:require [ring.util.response :as ring-resp]
            [mofficer.domain.business.user-config-business :as user-config-business]))

(defroutes user-config-api
  (context "/user-configs" []
           (POST "/" { body :body } 
                 (let [either-answer (user-config-business/create-user-config body)]
                   (if (:successAnswer either-answer) 
                     {:status 201 :body (:successAnswer either-answer)} 
                     {:status 500 :body (:errorMessage either-answer)})))
           (GET "/:sender-username" [sender-username] (ring-resp/response (user-config-business/get-user-config sender-username)))))
