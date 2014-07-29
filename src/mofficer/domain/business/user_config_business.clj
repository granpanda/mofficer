(ns mofficer.domain.business.user-config-business
  (:require [mofficer.persistence.user-config-dao :as user-config-dao]))
            ;[mofficer.domain.entities.user-config])
  ;(:import [mofficer.domain.entities.user-config UserConfig]))

(defn create-user-config [user-config]
  (user-config-dao/create-user-config user-config))

(defn get-user-config [sender-username]
  (user-config-dao/get-user-config sender-username))
