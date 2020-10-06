(ns backend.core-test
  (:require [clojure.test :refer :all]
            [backend.core :refer :all]))

(deftest convert-string-to-date-test
  (testing "convert-string-to-date-test"
    (is (= #inst "2015-10-09T21:00:00.000-00:00" (convert-string-to-date "2015-10-10")))))

(deftest convert-nil-to-date-test
  (testing "convert-nil-to-date-test"
    (is (= nil (convert-string-to-date nil)))))