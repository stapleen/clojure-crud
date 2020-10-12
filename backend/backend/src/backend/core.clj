(ns backend.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.jdbc :as jdbc]
            [backend.config :as config]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [bouncer.core :as b]
            [bouncer.validators :as v]))

(def db config/db-config)
(def port config/port)

(defn date-to-time-stamp [date]
  (-> date java.sql.Timestamp. .toLocalDateTime))

(defn convert-string-to-date [string]
  (if (nil? string) nil (java.sql.Date/valueOf string)))

(defn add-patients
  [request]
  (try
    (let [body (get-in request [:body])
          full-name (get-in body ["full_name"])
          gender (get-in body ["gender"])
          date-of-birth (get-in body ["date_of_birth"])
          current-date (.getTime (java.util.Date.))
          current-date-convert-time-stamp (date-to-time-stamp current-date)
          validator (b/validate body
                                ["full_name"]   [v/required v/string]
                                ["gender"] [v/required v/string [v/matches #"^М$|^Ж$"]]
                                ["date_of_birth"] [v/required
                                                   v/string
                                                   [v/matches #"^\d{4}\-(0[1-9]|1[012])\-(0[1-9]|[12][0-9]|3[01])$"]])]
      (if (nil? (first validator))
        (do
          (jdbc/insert! db :patients {:full_name full-name
                                      :gender gender
                                      :date_of_birth (convert-string-to-date date-of-birth)
                                      :created_at current-date-convert-time-stamp})
          (response {:success 1 :result "Успешно"}))
        (response {:success 0 :error "Некорректные данные"})))
    (catch Exception e (response {:success 0 :error "Ошибка"}))))

(defn get-patients [request]
(try
  (let [patients-list (jdbc/query db ["SELECT id, full_name, gender, date_of_birth 
  FROM patients WHERE deleted=false"])]
    (response {:success 1 :result patients-list}))
  (catch Exception e (response {:success 0 :error "Ошибка"}))))

(defn get-patient [request]
  (try
    (let
     [body (get-in request [:body])
      id (get-in request [:body "id"])
      patient (jdbc/query db ["SELECT id, full_name, gender, date_of_birth 
      FROM patients WHERE id = ? AND deleted=false" id])]
      (response {:success 1 :result patient}))
    (catch Exception e (response
                        {:success 0 :error "Ошибка"}))))

(defn delete-patient [request]
 (try
   (let [body (get-in request [:body])
         patient-id (get-in request [:body "id"])
         current-date (.getTime (java.util.Date.))
         current-date-convert-time-stamp (date-to-time-stamp current-date)
         query-result (jdbc/update! db :patients
                                    {:deleted true :updated_at current-date-convert-time-stamp}
                                    ["id = ?" patient-id])]  
     (if (zero? (first query-result))
       (response {:success 0 :error "Ошибка. Попробуйте повторить позже"})
       (response {:success 1 :result "Успешно"})))
   (catch Exception e (response {:success 0 :error "Ошибка"}))))

(defn update-patient-data
  [request]
  (try
    (let [body (get-in request [:body])
          id (get-in request [:body "id"])
          full-name (get-in request [:body "full_name"])
          gender (get-in request [:body "gender"])
          date-of-birth (get-in request [:body "date_of_birth"])
          date-of-birth-convert-date (convert-string-to-date date-of-birth)
          current-date (.getTime (java.util.Date.))
          current-date-convert-time-stamp (date-to-time-stamp current-date)
          query-result (jdbc/execute! db
                                      ["UPDATE patients SET full_name = COALESCE(?, full_name),
                   gender = COALESCE(?, gender),
                   date_of_birth = COALESCE(?, date_of_birth),
                   updated_at = ?
                   WHERE id = ?"
                                       full-name
                                       gender
                                       date-of-birth-convert-date
                                       current-date-convert-time-stamp
                                       id])]
      (if (zero? (first query-result))
        (response {:success 0 :error "Ошибка. Попробуйте повторить позже"})
        (response {:success 1 :result "Успешно"})))
    (catch Exception e (response {:success 0 :error "Ошибка"}))))

(defroutes app
  (POST "/add" [] (-> add-patients middleware/wrap-json-body middleware/wrap-json-response))
  (GET "/get" [] (-> get-patients middleware/wrap-json-response))
  (POST "/get/patient" [] (-> get-patient middleware/wrap-json-body middleware/wrap-json-response))
  (POST "/delete" [] (-> delete-patient middleware/wrap-json-body middleware/wrap-json-response))
  (POST "/update" [] (-> update-patient-data middleware/wrap-json-body middleware/wrap-json-response)))

(defn -main []
  (jetty/run-jetty (-> app (wrap-cors
                            :access-control-allow-origin [#".*"]
                            :access-control-allow-methods [:get :post]
                            :access-control-allow-credentials ["true"]))
                   {:port port}))