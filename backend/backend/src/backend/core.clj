(ns backend.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [backend.config :as config]
            [backend.controller :refer [add-patients get-patients get-patient
                                        delete-patient update-patient-data]]))

(def port config/port)

(defroutes app
  (POST "/patient/add" [] (-> add-patients wrap-json-body))
  (GET "/" [] (-> get-patients wrap-json-response))
  (GET "/patient" [] (-> get-patient wrap-params))
  (POST "/patient/delete" [] (-> delete-patient wrap-json-body))
  (POST "/patient/update" [] (-> update-patient-data wrap-json-body)))

(defn -main []
  (jetty/run-jetty (-> app (wrap-cors
                            :access-control-allow-origin [#".*"]
                            :access-control-allow-methods [:get :post]
                            :access-control-allow-credentials ["true"])
                       wrap-json-response)
                   {:port port}))