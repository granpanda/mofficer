(ns mofficer.domain.business.email-business-test
  (:use [midje.sweet])
  (:require [mofficer.domain.business.user-config-business :as user-config-business]
            [mofficer.domain.business.email-business :as email-business]
            [mofficer.infrastructure.datastructures.either])
  (:import [mofficer.infrastructure.datastructures.either Either]))


(def user-config-for-tests {:emailHost "http://www.gmail.com"
                            :emailPort 123
                            :senderUsername "senderUsername"
                            :senderPassword "senderPwd"
                            :senderEmail "sender@domain.com"
                            :senderId "senderId"})

(def email-info-for-tests {:senderEmail "sender@domain.com"
                           :recipients "one@domain.com"
                           :subject "subject"
                           :body "Halo!"})

(def mail-connection-for-tests {:host "http://www.gmail.com"
                                :ssl true
                                :user "senderUsername"
                                :pass "senderPwd"})

(def mail-message-for-tests {:from "sender@domain.com"
                             :to "one@domain.com"
                             :subject "subject"
                             :body "Halo!"})

(facts "About the email-business:"

       (fact "The get-mail-connection function returns the mail connection required by postal given an user-config"
             (email-business/get-mail-connection user-config-for-tests) => mail-connection-for-tests)
       
       (fact "The get-mail-message function returns the mail message required by postal given an email-info"
             (email-business/get-mail-message email-info-for-tests) => mail-message-for-tests))
