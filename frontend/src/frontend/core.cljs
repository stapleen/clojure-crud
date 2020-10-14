(ns frontend.core
  (:require
   [reagent.dom :as d]
   [frontend.components.patients :as patients]
   [frontend.components.patient.edit :as edit]
   [frontend.components.patient.add :as add]
   [frontend.routes :refer [app-routes app-state]]))

(defmulti current-page #(@app-state :page))

(defmethod current-page :home []
  [patients/component])

(defmethod current-page :new []
  [add/component])

(defmethod current-page :edit []
  (let [id (int (get-in @app-state [:id]))]
    [edit/component id]))

(defn init! []
  (app-routes)
  (d/render [current-page]
                  (.getElementById js/document "app")))