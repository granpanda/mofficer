(ns mofficer.persistence.user-config-dao
  (:require [clojure.java.jdbc :as jdbc-clj]))

(def mysql-db {:subprotocol "mysql"
               :subname "//localhost:3306/mofficerdb"
               :user "mofficer"
               :password "mofficer1234"})

(def user-config-table-name "user_configs")
(def create-table-if-not-exists-sql (str "CREATE TABLE IF NOT EXISTS " user-config-table-name " (emailHost VARCHAR(128), emailPort INT, senderUsername VARCHAR(128) PRIMARY KEY, senderPassword VARCHAR(128), senderEmail VARCHAR(128), senderId VARCHAR(128));"))

(defn create-user-configs-table-if-not-exists [db] (jdbc-clj/db-do-commands db create-table-if-not-exists-sql))

(defn count-table-rows [db] 
  (let [count-sql (str "SELECT COUNT (*) FROM " user-config-table-name)
        query-result (jdbc-clj/query db [count-sql])
        result-values (vals (first query-result))]
    (if (= 1 (count result-values)) (first result-values) 0)))

(defn create-user-config [db user-config]
  (let [insert-result (try (jdbc-clj/insert! db user-config-table-name user-config) 
                           (catch Exception e (.getMessage e)))]
    (if (and (seq? insert-result) (nil? (first insert-result))) true insert-result)))

(defn- get-same-identifier [string] string)

(defn get-user-config-by-username [db sender-username] 
  (let [select-sql (str "SELECT * FROM " user-config-table-name " WHERE senderUsername = ?;")
        query-result (jdbc-clj/query db [select-sql sender-username] :identifiers get-same-identifier)]
    (first query-result)))

(defn get-user-config-by-email [db sender-email] 
  (let [select-sql (str "SELECT * FROM " user-config-table-name " WHERE senderEmail = ?;")
        query-result (jdbc-clj/query db [select-sql sender-email] :identifiers get-same-identifier)]
    (first query-result)))

(defn get-user-config-by-sender-id [db sender-id] 
  (let [select-sql (str "SELECT * FROM " user-config-table-name " WHERE senderId = ?;")
        query-result (jdbc-clj/query db [select-sql sender-id] :identifiers get-same-identifier)]
    (first query-result)))
