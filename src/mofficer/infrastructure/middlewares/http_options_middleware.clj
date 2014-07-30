(ns mofficer.infrastructure.middlewares.http-options-middleware
  (:require [mofficer.infrastructure.utils.http-utils :as http-utils]))

(defn http-options-filter [app request]
  (let [http-verb (name (:request-method request))]
    (if (.equalsIgnoreCase http-verb "OPTIONS") 
      {:status 204 :headers http-utils/response-headers-with-cors} 
      (app request))))

(defn http-options-filter-middleware [app]
  (fn [request] (http-options-filter app request)))
