(defproject mofficer "1.0-SNAPSHOT"
  :description "Mofficer (for Mail Officer) is a system that does one thing: send e-mails."
  :url "https://github.com/granpanda/mofficer"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-jetty-adapter "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [ring-cors "0.1.0"]
                 [compojure "1.1.8"]
                 [cheshire "5.3.1"]
                 [clj-http "0.9.0"]
                 [clj-time "0.6.0"]
                 [com.draines/postal "1.11.1"]
                 [com.taoensso/timbre "4.1.4"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [org.clojure/java.jdbc "0.3.4"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler mofficer.main/app
         :stacktraces? true
         :auto-reload true
         :auto-refresh? true}
  :profiles
  {:dev {:plugins [[lein-midje "3.1.3"]]
         :dependencies [[javax.servlet/servlet-api "2.5"]
                        [com.h2database/h2 "1.4.180"]
                        [ring-mock "0.1.5"]
                        [midje "1.6.3"]]}}
  :main mofficer.main)
