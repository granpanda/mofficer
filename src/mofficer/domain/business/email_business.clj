(ns mofficer.domain.business.email-business
  (:require [cheshire.core :as ches]
            [postal.core :as postal]
            [mofficer.domain.business.user-config-business :as user-config-business]
            [mofficer.infrastructure.datastructures.either])
  (:import [mofficer.infrastructure.datastructures.either Either]))


(defn get-mail-connection [user-config] {:host (:emailHost user-config)
                                         :ssl true
                                         :user (:senderUsername user-config)
                                         :pass (:senderPassword user-config)})

(defn get-mail-message [email-info] {:from (:senderEmail email-info)
                                     :to (:recipients email-info)
                                     :subject (:subject email-info)
                                     :body (:body email-info)})

(defn- send-email-and-get-either-result [user-config email-info] 
  (let [mail-connection (get-mail-connection user-config)
        mail-message (get-mail-message email-info)]
    (try (Either. nil (postal/send-message mail-connection mail-message))
         (catch Exception e (Either. "There was a network problem, please try again later." nil)))))

(defn send-email [sender-id email-info] 
  (println "Sending email to: " (:recipients email-info) " with subject: " (:subject email-info) " and message: " (:body email-info))
  (let [sender-email (:senderEmail email-info)
        user-config (user-config-business/get-user-config-by-id sender-id)]
    (if (or (nil? sender-email) (nil? user-config))
      (Either. (str "The sender mail or the user config from the user with ID: " sender-id " does not exist.") nil)
      (send-email-and-get-either-result user-config email-info))))
