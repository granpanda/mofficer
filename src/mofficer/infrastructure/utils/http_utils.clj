(ns mofficer.infrastructure.utils.http-utils)

(def response-headers-with-cors 
  {"Access-Control-Allow-Headers" "X-Requested-With,Content-Type,Accept,Origin,Authorization"
   "Access-Control-Allow-Methods" "OPTIONS,HEAD,GET,POST,PUT,DELETE"
   "Access-Control-Allow-Origin" "*"})
