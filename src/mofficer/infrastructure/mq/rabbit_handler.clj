(ns mofficer.infrastructure.mq.rabbit-handler
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [cheshire.core :as ches]
            [mofficer.infrastructure.datastructures.either])
  (:import [mofficer.infrastructure.datastructures.either Either]))

(def default-exchange-name "")

(defn get-rabbit-connection []
  (try (Either. nil (rmq/connect {:automatically-recover true}))
       (catch Exception e (Either. (.getMessage e) nil))))

(defn get-rabbit-channel [connection]
  (if-not (nil? connection)
    (try (Either. nil (lch/open connection))
         (catch Exception e (Either. (.getMessage e) nil)))
    (Either. "The given connection is nil." nil)))

(defn publish-message-to-queue [connection channel queue-name message]
  (try (do (lq/declare channel queue-name :durable true :exclusive false :auto-delete false)
           (lb/publish channel default-exchange-name queue-name message)
           (println "Publish the message: " message ", to the queue: " queue-name ".")
           (rmq/close channel)
           (rmq/close connection)
           (Either. nil "The email has been sent."))
       (catch Exception e (Either. (.getMessage e) nil))))

(defn add-message-to-queue [queue-name message-as-clojure-map]
  (let [connection-either (get-rabbit-connection)
        connection (:successAnswer connection-either)
        channel-either (get-rabbit-channel connection)
        channel (:successAnswer channel-either)
        message-as-json (ches/generate-string message-as-clojure-map)]
    (if-not (and (nil? connection) (nil? channel))
      (publish-message-to-queue connection channel queue-name message-as-json)
      (Either. (str "Connection error: " (:errorMessage connection-either) ", Channel error: " (:errorMessage channel-either)) nil))))

(defn initialize-workers [number-of-workers queue-name handling-message-function]
  (if (> number-of-workers 0)
    (let [connection-either (get-rabbit-connection)
          connection (:successAnswer connection-either)
          channel-either (get-rabbit-channel connection)
          channel (:successAnswer channel-either)]
      (if-not (and (nil? connection) (nil? channel))
        (do (lb/qos channel 1)
            (lq/declare channel queue-name :durable true :exclusive false :auto-delete false)
            (lc/subscribe channel queue-name handling-message-function :auto-ack false)
            (println "Initialize email-worker #" number-of-workers)
            (initialize-workers (- number-of-workers 1) queue-name handling-message-function))))))
