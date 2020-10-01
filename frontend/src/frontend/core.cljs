(ns frontend.core
(:import [goog History]
           [goog.history EventType])
  (:require 
            [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [reagent.core :as reagent]
            [reagent.dom :as d]
            [frontend.patients :as patients]
            [frontend.patient.edit :as edit]
            [frontend.patient.add :as add]))

(def app-state (reagent/atom {}))

(defn app-routes []
  (secretary/set-config! :prefix "")

  (defroute "/" []
    (swap! app-state assoc :page :home))

  (defroute "/new" []
    (swap! app-state assoc :page :new))

  (defroute "/edit/:id" [id]
    (swap! app-state assoc :page :edit :id id))

    (doto (History.)
      (events/listen EventType.NAVIGATE #(secretary/dispatch! (.-token %)))
      (.setEnabled true))
)

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