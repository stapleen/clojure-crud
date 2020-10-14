(ns frontend.routes
  (:import [goog History]
           [goog.history EventType])
  (:require
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.events :as events]
   [reagent.core :as reagent]))

(def route-slug {:home "/" :new "/new" :edit "/edit"})

(def home (get-in route-slug [:home]))
(def patient-new (get-in route-slug [:new]))
(def patient-edit (get-in route-slug [:edit]))

(def app-state (reagent/atom {}))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute (get-in route-slug [:home]) []
    (swap! app-state assoc :page :home))

  (defroute (get-in route-slug [:new]) []
    (swap! app-state assoc :page :new))

  (defroute (str (get-in route-slug [:edit]) "/:id") [id]
    (swap! app-state assoc :page :edit :id id))

  (doto (History.)
    (events/listen EventType.NAVIGATE #(secretary/dispatch! (.-token %)))
    (.setEnabled true)))