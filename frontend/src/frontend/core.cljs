(ns frontend.core
  (:require
   [reagent.dom :as d]
   [frontend.components.patients :as patients]
   [frontend.components.patient.edit :as edit]
   [frontend.components.patient.add :as add]
   [frontend.routes :refer [app-routes app-state]]))

(defn home []
  [patients/component])

(defn new []
  [add/component])

(defn edit [id]
  [edit/component id])

(defmulti current-page #(@app-state :page))

(defmethod current-page :home []
  [home])

(defmethod current-page :new []
  [new])

(defmethod current-page :edit []
  [edit (int (get-in @app-state [:id]))])

(defn init! []
  (app-routes)
  (d/render [current-page]
                  (.getElementById js/document "app")))