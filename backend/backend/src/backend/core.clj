(ns backend.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes app
  (POST "/add" [] "Пациент добавлен")
  (route/not-found "<h1>Page not found</h1>"))

(defn -main []
  (jetty/run-jetty app {:port 3000}))