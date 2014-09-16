(ns mofficer.domain.business.email-business
  (:require [cheshire.core :as ches]
            [postal.core :as postal]
            [mofficer.domain.business.user-config-business :as user-config-business]))


(defn get-mail-connection [user-config] {:host (:emailHost user-config)
                                         :ssl true
                                         :user (:senderUsername user-config)
                                         :pass (:senderPassword user-config)})

(defn get-mail-message [email-info] {:from (:senderEmail email-info)
                                     :to (:recipients email-info)
                                     :subject (:subject email-info)
                                     :body (:body email-info)})

(defn- send-email-and-get-result [user-config email-info] 
  (let [mail-connection (get-mail-connection user-config)
        mail-message (get-mail-message email-info)]
    (postal/send-message mail-connection mail-message)))

(defn send-email [sender-id email-info] 
  (println "Sending email to: " (:recipients email-info) " with subject: " (:subject email-info) " and message: " (:body email-info))
  (let [sender-email (:senderEmail email-info)
        user-config (user-config-business/get-user-config-by-email sender-email)]
    (send-email-and-get-result user-config email-info)))
