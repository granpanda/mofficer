(ns mofficer.persistence.user-config-dao-test
  (:use [midje.sweet])
  (:require [clojure.java.jdbc :as jdbc-clj]
            [mofficer.domain.entities.user-config]
            [mofficer.persistence.user-config-dao :as user-config-dao])
  (:import [mofficer.domain.entities.user_config UserConfig]))

(def h2-db {:subprotocol "h2"
            :subname "jdbc:h2:tcp://localhost/mem:testdb;DATABASE_TO_UPPER=FALSE"
            :user "sa"
            :password ""})

(def user-config-test {:emailHost "http://www.gmail.com"
                       :emailPort 123
                       :senderUsername "senderUsername"
                       :senderPassword "senderPwd"
                       :senderEmail "sender@domain.com"
                       :senderId "senderId"})

(defn drop-db [db]
  (let [drop-table-sql (str "DROP TABLE IF EXISTS " user-config-dao/user-config-table-name)]
    (jdbc-clj/db-do-commands db drop-table-sql)))

(background (before :facts (user-config-dao/create-user-configs-table-if-not-exists h2-db))
            (after :facts (drop-db h2-db)))

(facts "About the user-config-dao:"

       (fact "create-user-config creates a new user config into the database."
             (user-config-dao/count-table-rows h2-db) => 0
             (user-config-dao/create-user-config h2-db user-config-test) => true
             (user-config-dao/count-table-rows h2-db) => 1)

       (fact "get-user-config-by-username returns the user-config with the given username."
             (user-config-dao/count-table-rows h2-db) => 0
             (user-config-dao/create-user-config h2-db user-config-test) => true
             (user-config-dao/count-table-rows h2-db) => 1
             (user-config-dao/get-user-config-by-username h2-db "senderUsername") => user-config-test)
       
       (fact "get-user-config-by-email returns the user-config with the given email."
             (user-config-dao/count-table-rows h2-db) => 0
             (user-config-dao/create-user-config h2-db user-config-test) => true
             (user-config-dao/count-table-rows h2-db) => 1
             (user-config-dao/get-user-config-by-email h2-db "sender@domain.com") => user-config-test)
       
       (fact "get-user-config-by-id returns the user-config with the given ID."
             (user-config-dao/count-table-rows h2-db) => 0
             (user-config-dao/create-user-config h2-db user-config-test) => true
             (user-config-dao/count-table-rows h2-db) => 1
             (user-config-dao/get-user-config-by-id h2-db "senderId") => user-config-test))
