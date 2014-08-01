(ns mofficer.service.user-config-resource-test
  (:use [midje.sweet])
  (:require [mofficer.infrastructure.datastructures.either]
            [cheshire.core :as ches]
            [mofficer.domain.business.user-config-business :as user-config-business]
            [mofficer.service.user-config-resource :as user-config-resource])
  (:import [mofficer.infrastructure.datastructures.either Either]))

(def user-config-test {:emailHost "http://www.gmail.com"
                       :emailPort 123
                       :senderUsername "senderUsername"
                       :senderPassword "senderPwd"
                       :senderEmail "sender@domain.com"
                       :senderId "senderId"})

(def user-config-test-as-json (ches/generate-string user-config-test))

(defn get-request-map [http-method uri body] 
  {:request-method http-method
   :uri uri
   :body body})

(facts "About the user config resource:"

       (fact "Make a POST over /user-configs answer 201 if the user was created."
             (user-config-resource/user-config-api (get-request-map :post "/user-configs" user-config-test-as-json)) => (contains {:status 201 :body user-config-test})
             (provided (user-config-business/create-user-config anything) => (Either. nil user-config-test)))

       (fact "Make a POST over /user-configs answer 500 if the user couldn't be created."
             (user-config-resource/user-config-api (get-request-map :post "/user-configs" user-config-test-as-json)) => (contains {:status 500})
             (provided (user-config-business/create-user-config anything) => (Either. anything nil)))

       (fact "Make a GET over /user-configs/:sender-username returns the user-config with the same username."
             (user-config-resource/user-config-api (get-request-map :get "/user-configs/senderUsername" nil)) => (contains {:status 200 :body user-config-test})
             (provided (user-config-business/get-user-config-by-username "senderUsername") => user-config-test))

       (fact "Make a GET over /user-configs/:sender-username returns nil if the user-config with the same username does not exist."
             (user-config-resource/user-config-api (get-request-map :get "/user-configs/unknownUsername" nil)) => (contains {:status 200 :body nil})
             (provided (user-config-business/get-user-config-by-username "unknownUsername") => nil)))
