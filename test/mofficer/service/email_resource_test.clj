(ns mofficer.service.email-resource-test
  (:use [midje.sweet])
  (:require [mofficer.infrastructure.datastructures.either]
            [cheshire.core :as ches]
            [mofficer.domain.business.user-config-business :as user-config-business]
            [mofficer.domain.business.email-business :as email-business]
            [mofficer.service.email-resource :as email-resource])
  (:import [mofficer.infrastructure.datastructures.either Either]))

(def user-config-test {:emailHost "http://www.gmail.com"
                       :emailPort 123
                       :senderUsername "granpanda"
                       :senderPassword "gpwd"
                       :senderEmail "granpanda@granpanda.com"
                       :senderId "granpanda"})

(def email-info-test {:senderEmail "granpanda@granpanda.com"
                      :recipients ["someone@domain.com"]
                      :subject "A subject."
                      :body "A body."})

(def email-info-test-as-json (ches/generate-string email-info-test))

(defn get-request-map [http-method uri body] {:request-method http-method :uri uri :body body})

(facts "About the email resource:"

       (fact "Make a POST over /emails/:sender-id returns 202 if the operation was successful."
             (email-resource/email-api (get-request-map :post "/emails/granpanda" email-info-test-as-json)) => (contains {:status 202})
             (provided (user-config-business/get-user-config-by-email anything) => user-config-test
                       (email-business/send-email anything anything) => true))

       (fact "Make a POST over /emails/:sender-id returns 500 if the operation was not successful."
             (email-resource/email-api (get-request-map :post "/emails/granpanda" email-info-test-as-json)) => (contains {:status 500})
             (provided (user-config-business/get-user-config-by-email anything) => nil)))
