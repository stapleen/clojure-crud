(ns backend.controller
  (:require [clojure.java.jdbc :as jdbc]
            [backend.config :as config]
            [ring.util.response :refer [response bad-request status]]
            [bouncer.core :as b]
            [bouncer.validators :as v]))

(def db config/db-config)

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
                                ["full_name"] [v/required v/string]
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
        (bad-request {:success 0 :error "Некорректные данные"})))
    (catch Exception e (status 500))))

(defn get-patients [request]
  (try
    (let [patients-list (jdbc/query db ["SELECT id, full_name, gender, date_of_birth 
  FROM patients WHERE deleted=false"])]
      (response {:success 1 :result patients-list}))
    (catch Exception e (status 500))))

(defn get-patient [request]
  (try
    (let [id (Integer. (get-in request [:query-params "id"]))
          patient (jdbc/query db ["SELECT id, full_name, gender, date_of_birth 
      FROM patients WHERE id = ? AND deleted=false" id])]
      (response {:success 1 :result patient}))
    (catch Exception e (status 400))))

(defn delete-patient [request]
 (try
   (let [patient-id (get-in request [:body "id"])
         current-date (.getTime (java.util.Date.))
         current-date-convert-time-stamp (date-to-time-stamp current-date)]
     (jdbc/update! db :patients
                   {:deleted true :updated_at current-date-convert-time-stamp}
                   ["id = ?" patient-id])
     (response {:success 1 :result "Успешно"}))
   (catch Exception e (status 500))))

(defn update-patient-data
  [request]
  (try
    (let [body (get-in request [:body])
          id (get-in body ["id"])
          full-name (get-in body ["full_name"])
          gender (get-in body ["gender"])
          date-of-birth (get-in body ["date_of_birth"])
          current-date (.getTime (java.util.Date.))
          current-date-convert-time-stamp (date-to-time-stamp current-date)
          validator (b/validate body
                                ["full_name"] [v/required v/string]
                                ["gender"] [ v/string [v/matches #"^М$|^Ж$"]]
                                ["date_of_birth"] [v/string
                                                   [v/matches #"^\d{4}\-(0[1-9]|1[012])\-(0[1-9]|[12][0-9]|3[01])$"]])]
      (if (nil? (first validator))
        (do
          (jdbc/execute! db
                         ["UPDATE patients SET full_name = COALESCE(?, full_name),
                   gender = COALESCE(?, gender),
                   date_of_birth = COALESCE(?, date_of_birth),
                   updated_at = ?
                   WHERE id = ?"
                          full-name
                          gender
                          (convert-string-to-date date-of-birth)
                          current-date-convert-time-stamp
                          id])
          (response {:success 1 :result "Успешно"}))
        (bad-request {:success 0 :error "Некорректные данные"})))
    (catch Exception e (status 500))))