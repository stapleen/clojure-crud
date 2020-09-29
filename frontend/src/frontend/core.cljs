(ns frontend.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]))

(defn patients
  []
  (let [patient (r/atom nil)]

    (r/create-class                 
     {:display-name  "patients"      

      :component-did-mount              
      (fn [this]
        (go (let [response (<! (http/get "http://localhost:3000/get"))
                  result (get-in response [:body :result])]
              ;; (reset! patient result) для нескольких пациентов
              (swap! patient result))))
              


      :reagent-render        
      (fn []


        (let [id (get-in @patient [:id])
              full-name (get-in @patient [:full-name])
              gender (get-in @patient [:gender])
              date_of_birth (get-in @patient [:date_of_birth])]
          (println "id" id "full-name" full-name "gender" gender "date_of_birth" date_of_birth)
          [:div
           [:p "id: " id]
           [:p "full-name: " full-name]
           [:p "gender: " gender]
           [:p "date_of_birth: " date_of_birth]]))})))


(defn homepage []
  [:div
   [:h1 "Welcome"]
   [patients]])

(defn init! []
  (d/render [homepage] (.getElementById js/document "app")))


