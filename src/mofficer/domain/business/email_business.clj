(ns mofficer.domain.business.email-business
  (:require [mofficer.infrastructure.mq.rabbit-handler :as rabbit-handler]))

(def email-queue-name "mofficer_email-queue")

(def add-message-to-email-queue [user-config email-info]
  (let [message-as-clojure-map {:user-config user-config :email-info email-info}]
    (rabbit-handler/add-message-to-queue email-queue-name message-as-clojure-map)))
