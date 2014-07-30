(ns mofficer.persistence.user-config-dao
  (:require [clojure.java.jdbc :as jdbc-clj]
            [mofficer.infrastructure.datastructures.either])
  (:import [mofficer.infrastructure.datastructures.either Either]))

(def mysql-db {:subprotocol "mysql"
               :subname "//localhost:3306/mofficerdb"
               :user "mofficer"
               :password "mofficer1234"})

(def user-config-table-name "user_configs")
(def create-table-if-not-exists-sql (str "CREATE TABLE IF NOT EXISTS " user-config-table-name " (emailHost VARCHAR(256), emailPort INT, senderUsername VARCHAR(256) PRIMARY KEY," 
                                            " senderPassword VARCHAR(256), senderEmail VARCHAR(256), senderId VARCHAR(128));"))

(defn create-user-configs-table-if-not-exists [] (jdbc-clj/db-do-commands mysql-db create-table-if-not-exists-sql))

(defn create-user-config [user-config]
  (let [insert-result (try (jdbc-clj/insert! mysql-db user-config-table-name user-config) 
                           (catch Exception e (.getMessage e)))]
    (if (and (seq? insert-result) (nil? (first insert-result))) nil insert-result)))

(defn- get-same-identifier [string] string)

(defn get-user-config-by-username [sender-username] 
  (let [select-sql (str "SELECT * FROM " user-config-table-name " WHERE senderUsername = ?;")]
    (jdbc-clj/query mysql-db [select-sql sender-username] :identifiers get-same-identifier)))

(defn get-user-config-by-email [sender-email] 
  (let [select-sql (str "SELECT * FROM " user-config-table-name " WHERE senderEmail = ?;")]
    (jdbc-clj/query mysql-db [select-sql sender-email] :identifiers get-same-identifier)))

(defn get-user-config-by-id [sender-id] 
  (let [select-sql (str "SELECT * FROM " user-config-table-name " WHERE senderId = ?;")]
    (jdbc-clj/query mysql-db [select-sql sender-id] :identifiers get-same-identifier)))
