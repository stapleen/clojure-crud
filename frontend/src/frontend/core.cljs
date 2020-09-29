(ns frontend.core
  (:require
   [reagent.dom :as d]
   [frontend.patients :as patients]
   ))

(defn app []
  [:div
   [:h1 "Welcome"]
   [patients/component]
   ])

(defn init! []
  (d/render [app] (.getElementById js/document "app")))


