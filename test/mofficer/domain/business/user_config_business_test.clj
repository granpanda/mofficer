(ns mofficer.domain.business.user-config-business-test
  (:use [midje.sweet])
  (:require [mofficer.infrastructure.datastructures.either]
            [mofficer.persistence.user-config-dao :as user-config-dao]
            [mofficer.domain.business.user-config-business :as user-config-business])
  (:import [mofficer.infrastructure.datastructures.either Either]))

(def user-config-for-tests {:emailHost "http://www.gmail.com"
                            :emailPort 123
                            :senderUsername "senderUsername"
                            :senderPassword "senderPwd"
                            :senderEmail "sender@domain.com"
                            :senderId "senderId"})

(facts "About the user-config-business:"

       (fact "the create-user-config function returns an either data structure with a success answer when the insert was correct."
             (user-config-business/create-user-config user-config-for-tests) => (Either. nil user-config-for-tests)
             (provided (user-config-dao/create-user-config anything user-config-for-tests) => true))

       (fact "the create-user-config function returns an either data structure with an error message when the insert was not successful."
             (user-config-business/create-user-config user-config-for-tests) => (Either. "The username is already into the DB." nil)
             (provided (user-config-dao/create-user-config anything user-config-for-tests) => false))

       (fact "The get-user-config-by-sender-id function returns the matching user config."
             (user-config-business/get-user-config-by-sender-id "senderId") => user-config-for-tests
             (provided (user-config-dao/get-user-config-by-sender-id anything "senderId") => user-config-for-tests))

       (fact "The get-user-config-by-username function returns the matching user config."
             (user-config-business/get-user-config-by-username "senderUsername") => user-config-for-tests
             (provided (user-config-dao/get-user-config-by-username anything "senderUsername") => user-config-for-tests))

       (fact "The get-user-config-by-email function returns the matching user config."
             (user-config-business/get-user-config-by-email "sender@domain.com") => user-config-for-tests
             (provided (user-config-dao/get-user-config-by-email anything "sender@domain.com") => user-config-for-tests)))
