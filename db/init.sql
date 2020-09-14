CREATE TABLE patients (
  id INT PRIMARY KEY,
  full_name VARCHAR(50) NOT NULL,
  gender VARCHAR(1) NOT NULL,
  date_of_birth DATE NOT NULL,
  deleted BOOL DEFAULT FALSE,
  created_at DATE,
  updated_at DATE
);