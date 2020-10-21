(ns backend.config
  (:require [environ.core :refer [env]]))

(def type-env (env :clojure-env))

(def db-config {:dbtype "postgresql"
                :dbname (if (not (= type-env "prod")) "clojure_test" "clojure")
                :host "localhost"
                :user "postgres"
                :password "root"})

(def port 3000)
