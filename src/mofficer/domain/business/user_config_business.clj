(ns mofficer.domain.business.user-config-business
  (:require [clojure.string :as clj-str]
            [mofficer.persistence.user-config-dao :as user-config-dao]
            [mofficer.domain.entities.either])
  (:import [mofficer.domain.entities.either Either]))

(defn create-either [errorMessage successAnswer] (Either. errorMessage successAnswer))

(defn create-user-config [user-config]
  (let [errorMessage (user-config-dao/create-user-config user-config)]
    (if (nil? errorMessage) 
      (create-either errorMessage user-config) 
      (create-either "The username is already into the DB." nil))))

(defn get-user-config [sender-username]
  (first (user-config-dao/get-user-config sender-username)))
