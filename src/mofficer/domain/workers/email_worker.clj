(ns mofficer.domain.workers.email-worker
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [cheshire.core :as ches]
            [postal.core :as postal]))

(def utf-8 "UTF-8")
(def number-of-workers 10)

(defn send-email [user-config email-info] 
  (println "Sending email to: " (:recipients email-info) " with subject: " (:subject email-info) " and message: " (:body email-info))
  (let [postal-answer (postal/send-message { :host (:emailHost user-config)
                         :port (:emailPort user-config)
                         :user (:senderUsername user-config)
                         :pass (:senderPassword user-config) }
                       { :from (:senderEmail email-info) 
                         :to (:recipients email-info) 
                         :subject (:subject email-info)
                         :body (:body email-info) })]
    (if (= 0 (:code postal-answer)) true false)))

(defn send-message-from-queue-by-email [channel metadata ^bytes payload]
  (let [message-as-json (String. payload utf-8)
        message-as-clojure-map (ches/parse-string message-as-json true)
        user-config (:user-config message-as-clojure-map)
        email-info (:email-info message-as-clojure-map)]
    (if (send-email user-config email-info)
      (lb/ack channel (:delivery-tag metadata)))))

(defn handle-queue-messages [number-of-workers queue-name handling-message-function]
  (if (> number-of-workers 0)
    (let [connection (rmq/connect)
          channel (lch/open connection)]
      (lb/qos channel 1)
      (lq/declare channel queue-name :exclusive false :auto-delete false)
      (lc/subscribe channel queue-name handling-message-function :auto-ack false)
      (handle-queue-messages (- number-of-workers 1) queue-name handling-message-function))))
