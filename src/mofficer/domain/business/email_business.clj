(ns mofficer.domain.business.email-business
  (:require [cheshire.core :as ches]
            [postal.core :as postal]
            [mofficer.domain.business.user-config-business :as user-config-business]))

(def utf-8 "UTF-8")

(defn get-message [user-config email-info] {:host (:emailHost user-config), :port (:emailPort user-config), :user (:senderUsername user-config), :pass (:senderPassword user-config)}
                                           {:from (:senderEmail email-info), :to (:recipients email-info), :subject (:subject email-info), :body (:body email-info)})

(defn send-email-result [user-config email-info] 
  (let [postal-answer (postal/send-message (get-message user-config email-info))]
    (if (= 0 (:code postal-answer)) true false)))


(defn send-email [user-config email-info] 
  (println "Sending email to: " (:recipients email-info) " with subject: " (:subject email-info) " and message: " (:body email-info))
  (let [send-email (:senderEmail email-info)
        user-config (user-config-business/get-user-config-by-email sender-email)]
    (if (= sender-id (:senderId user-config))
      send-email-result
      false)))
