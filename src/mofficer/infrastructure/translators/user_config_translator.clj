(ns mofficer.infrastructure.translators.user-config-translator
  (:require [mofficer.domain.entities.user-config :as user-config])
  (:import [mofficer.domain.entities.user_config UserConfig]))

(defn get-user-config-record-from-map [clojure-map]
  (UserConfig. (:emailHost clojure-map)
               (:emailPort clojure-map)
               (:senderUsername clojure-map)
               (:senderPassword clojure-map)
               (:senderEmail clojure-map)
               (:senderId clojure-map)))
