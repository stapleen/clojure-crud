(ns frontend.core
  (:require
   [reagent.dom :as d]
   [frontend.patients :as patients]
   [frontend.patient.edit :as edit]
   [frontend.patient.add :as add]

   ))

(defn app []
  [:div
   [patients/component]
   ])

(defn init! []
  (d/render [app] (.getElementById js/document "app")))