CREATE DATABASE clojure;

 \c clojure 

CREATE TABLE patients (
  id SERIAL NOT NULL PRIMARY KEY,
  full_name VARCHAR(50) NOT NULL,
  gender VARCHAR(1) NOT NULL,
  date_of_birth DATE NOT NULL,
  deleted BOOL NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ  
);

INSERT INTO patients (full_name, gender, date_of_birth) values ('Вадим', 'М', '2020-09-15'),
('Тестовый Вадим', 'Ж', '2010-01-01');