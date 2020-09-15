(ns backend.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.jdbc :as jdbc]
            [backend.config :as config]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response]]))

(def db config/db-config)

(defn add-patients
  [request]
  (let [body (get-in request [:body])
        full-name (get-in request [:body "full_name"])
        gender (get-in request [:body "gender"])
        date-of-birth (get-in request [:body "date_of_birth"])
        date-of-birth-convert-date (java.sql.Date/valueOf date-of-birth)
        current-date (.getTime (java.util.Date.))
        current-date-convert-date (-> current-date java.sql.Timestamp. .toLocalDateTime)]

    (jdbc/insert! db :patients {:full_name full-name
                                :gender gender
                                :date_of_birth date-of-birth-convert-date
                                :created_at current-date-convert-date})
    (response {:success 1})))
   
(defroutes app
  (POST "/add" [] (-> add-patients middleware/wrap-json-body middleware/wrap-json-response))
  (route/not-found "<h1>Page not found</h1>"))

(defn -main []
  (jetty/run-jetty app {:port 3000}))