(ns backend.config)

(def db-config {:dbtype "postgresql"
                :dbname "clojure"
                :host "db"
                :user "postgres"
                :password "password"})

(def port 3000)