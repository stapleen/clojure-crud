(ns backend.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.jdbc :as jdbc]
            [backend.config :as config]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response]]
            [ring.middleware.cors :refer [wrap-cors]]))

(def db config/db-config)

(defn add-patients
  [request]
  (try
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
      (response {:success 1 :result "Успешно"}))

    (catch Exception e (response {:success 0 :error "Произошла ошибка"}))))
   
(defn get-patients [request]
(try
  (let [patients-list (jdbc/query db ["SELECT id, full_name, gender, date_of_birth FROM patients WHERE deleted=false"])]
    (response {:success 1 :result patients-list}))
    
  (catch Exception e (response {:success 0 :error "Произошла ошибка"}))))

(defn delete-patient [request]
 (try
   (let [body (get-in request [:body])
         patient-id (get-in request [:body "id"])
         current-date (.getTime (java.util.Date.))
         current-date-convert-date (-> current-date java.sql.Timestamp. .toLocalDateTime)
         query-result (jdbc/update! db :patients {:deleted true :updated_at current-date-convert-date} ["id = ?" patient-id])]

     (if (zero? (first query-result))
      (response {:success 0 :error "Ошибка. Попробуйте повторить позже"})
      (response {:success 1 :result "Успешно"})))

   (catch Exception e (response {:success 0 :error "Произошла ошибка"}))))

(defn update-patient-data
  [request]
  (try
    (let [body (get-in request [:body])
          id (get-in request [:body "id"])
          full-name (get-in request [:body "full_name"])
          gender (get-in request [:body "gender"])
          date-of-birth (get-in request [:body "date_of_birth"])
          date-of-birth-convert-date (if (nil? date-of-birth) nil (java.sql.Date/valueOf date-of-birth))
          current-date (.getTime (java.util.Date.))
          current-date-convert-date (-> current-date java.sql.Timestamp. .toLocalDateTime)
          query-result (jdbc/execute! db
                                      ["UPDATE patients SET full_name = COALESCE(?, full_name),
                   gender = COALESCE(?, gender),
                   date_of_birth = COALESCE(?, date_of_birth),
                   updated_at = ?
                   WHERE id = ?" full-name gender date-of-birth-convert-date current-date-convert-date id])]

      (if (zero? (first query-result))
        (response {:success 0 :error "Ошибка. Попробуйте повторить позже"})
        (response {:success 1 :result "Успешно"})))

    (catch Exception e (response {:success 0 :error "Произошла ошибка"}))))

(defroutes app
  (POST "/add" [] (-> add-patients middleware/wrap-json-body middleware/wrap-json-response))
  (GET "/get" [] (-> get-patients middleware/wrap-json-response))
  (POST "/delete" [] (-> delete-patient middleware/wrap-json-body middleware/wrap-json-response))
  (POST "/update" [] (-> update-patient-data middleware/wrap-json-body middleware/wrap-json-response)))

(defn -main []
  (jetty/run-jetty (-> app (wrap-cors :access-control-allow-origin [#".*"] :access-control-allow-methods [:get :post] :access-control-allow-credentials ["true"])) {:port 3000}))