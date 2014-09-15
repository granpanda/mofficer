(ns mofficer.service.user-config-resource
  (:use [compojure.core])
  (:require [ring.util.response :as ring-resp]
            [mofficer.domain.business.user-config-business :as user-config-business]
            [mofficer.infrastructure.translators.user-config-translator :as user-config-translator]))

(defn create-user-config [user-config-map]
  (let [user-config-record (user-config-translator/get-user-config-record-from-map user-config-map)
        either-answer (user-config-business/create-user-config user-config-record)]
    (if (:successAnswer either-answer) 
      { :status 201 :body (:successAnswer either-answer) } 
      { :status 500 :body (:errorMessage either-answer) })))

(defroutes user-config-api
  (context "/user-configs" []
           (POST "/" { user-config-map :body } (create-user-config user-config-map))
           (GET "/:sender-username" [sender-username] (ring-resp/response (user-config-business/get-user-config-by-username sender-username)))))
