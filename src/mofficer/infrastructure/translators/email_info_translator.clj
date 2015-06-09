(ns mofficer.infrastructure.translators.email-info-translator
  (:require [mofficer.domain.entities.email-info])
  (:import [mofficer.domain.entities.email_info EmailInfo]))

(defn get-email-info-from-map [clojure-map]
  (EmailInfo. (:recipients clojure-map)
              (:subject clojure-map)
              (:body clojure-map)))
