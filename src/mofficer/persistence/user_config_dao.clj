(ns mofficer.persistence.user-config-dao
  (:require [clojure.string :as clj-str] 
            [clojure.java.jdbc :as jdbc-clj]))

(def mysql-db {:subprotocol "mysql"
               :subname "//localhost:3306/mofficerdb"
               :user "mofficer"
               :password "mofficer1234"})

(def user-config-table-name "user_configs")

(defn create-user-configs-table-if-not-exists []
  (let [create-table-if-not-exists-sql (str "CREATE TABLE IF NOT EXISTS " user-config-table-name " (emailHost VARCHAR(256), emailPort INT, senderUsername VARCHAR(256) PRIMARY KEY," 
                                            " senderPassword VARCHAR(256), senderEmail VARCHAR(256));")]
    (jdbc-clj/db-do-commands mysql-db create-table-if-not-exists-sql)))

(defn create-user-config [user-config]
  (let [insert-response (try 
                          (jdbc-clj/insert! mysql-db user-config-table-name user-config) 
                          (catch Exception e (.getMessage e)))]
    (clj-str/blank? insert-response)))

(defn get-user-config [sender-username] 
  (let [select-sql (str "SELECT * FROM " user-config-table-name " WHERE senderUsername = ?;")]
    (jdbc-clj/query mysql-db [select-sql sender-username])))