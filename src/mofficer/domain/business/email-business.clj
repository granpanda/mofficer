(ns mofficer.domain.business.email-business
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [cheshire.core :as ches]))

(def default-exchange-name "")
(def email-queue-name "mofficer_email-queue")

(defn queue-email-request [user-config email-info]
  (let [connection (rmq/connect)
        channel (lch/open connection)
        message (ches/generate-string {:user-config user-config :email-info email-info})]
    (lq/declare channel email-queue-name :exclusive false :auto-delete false)
    (lb/publish channel default-exchange-name email-queue-name message)
    (rmq/close channel)
    (rmq/close connection)))
