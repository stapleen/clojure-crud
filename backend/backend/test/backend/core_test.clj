(ns backend.core-test
  (:require [clojure.test :refer :all]
            [backend.core :refer :all]
            [clojure.java.jdbc :as jdbc]))

(deftest convert-string-to-date-test
  (testing "convert-string-to-date-test"
    (is (= #inst "2015-10-09T21:00:00.000-00:00" (convert-string-to-date "2015-10-10")))))

(deftest convert-nil-to-date-test
  (testing "convert-nil-to-date-test"
    (is (= nil (convert-string-to-date nil)))))

(deftest insert-patient-test
  (testing "insert-patient-test"
    (let [request {:body {"full_name" "unique_test_name", "gender" "M", "date_of_birth" "2015-10-10"}}]
      (add-patients request)
      (let [result (jdbc/query db ["SELECT full_name FROM patients WHERE full_name='unique_test_name'"])
            full-name (get-in (first result) [:full_name])]
        (is (= "unique_test_name" full-name))
        (jdbc/delete! db :patients ["full_name = 'unique_test_name'"])))))

(deftest insert-patient-test-2
  (testing "insert-patient-test-2"
    (let [request {:body {"full_name" "unique_test_name-null", "gender" nil, "date_of_birth" "2015-10-10"}}]
      (add-patients request)
      (let [result (jdbc/query db ["SELECT full_name FROM patients WHERE full_name='unique_test_name-null'"])
            full-name (get-in (first result) [:full_name])]
        (is (= nil full-name))
        (jdbc/delete! db :patients ["full_name = 'unique_test_name-null'"])))))

(def patient (jdbc/insert! db :patients {:full_name "Test"
                                         :gender "М"
                                         :date_of_birth (convert-string-to-date "2020-01-01")}))
(def id-patient (get-in (first patient) [:id]))

(deftest select-patients-test
  (testing "select-patients-test"
    (let [result (get-in (get-patients []) [:body :result])]
      (is (= false (empty? result))))))

(deftest select-patient-test
  (testing "select-patient-test"
    (let [request {:body {"id" id-patient}}
          result (get-in (get-patient request) [:body :result])]
      (is (= false (empty? result))))))

;; (jdbc/delete! db :patients ["id = ?" id-patient])