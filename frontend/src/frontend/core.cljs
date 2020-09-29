(ns frontend.core
  (:require
   [reagent.dom :as d]
   [frontend.patients :as patients]
   [frontend.patient.edit :as edit]
   ))

(defn app []
  [:div
   [edit/component]
   ])

(defn init! []
  (d/render [app] (.getElementById js/document "app")))