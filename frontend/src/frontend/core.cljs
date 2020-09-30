(ns frontend.core
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.history.Html5History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [reagent.core :as reagent]
            [reagent.dom :as d]
            [frontend.patients :as patients]
            [frontend.patient.edit :as edit]
            [frontend.patient.add :as add]))

(def app-state (reagent/atom {}))

(defn hook-browser-navigation! []
  (doto (Html5History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "")

  (defroute "/" []
    (swap! app-state assoc :page :home))

  (defroute "/new" []
    (swap! app-state assoc :page :new))

  (defroute "/edit" []
    (swap! app-state assoc :page :edit))

  (hook-browser-navigation!))

(defn home []
  [patients/component])

(defn new []
  [add/component])

(defn edit []
  [edit/component])

(defmulti current-page #(@app-state :page))

(defmethod current-page :home []
  [home])

(defmethod current-page :new []
  [new])

(defmethod current-page :edit []
  [edit])

(defn init! []
  (app-routes)
  (d/render [current-page]
                  (.getElementById js/document "app")))