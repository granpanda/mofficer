(ns mofficer.domain.business.user-config-business
  (:require [clojure.string :as clj-str]
            [mofficer.persistence.user-config-dao :as user-config-dao]
            [mofficer.infrastructure.datastructures.either])
  (:import [mofficer.infrastructure.datastructures.either Either]))

(defn create-user-config [user-config]
  (let [insert-result (user-config-dao/create-user-config user-config-dao/mysql-db user-config)]
    (if (true? insert-result) 
      (Either. nil user-config) 
      (Either. "The username is already into the DB." nil))))

(defn get-user-config-by-sender-id [sender-id]
  (user-config-dao/get-user-config-by-sender-id user-config-dao/mysql-db sender-id))

(defn get-user-config-by-username [sender-username]
  (user-config-dao/get-user-config-by-username user-config-dao/mysql-db sender-username))

(defn get-user-config-by-email [sender-email]
  (user-config-dao/get-user-config-by-email user-config-dao/mysql-db sender-email))
