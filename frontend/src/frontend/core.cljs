(ns frontend.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]))

;; (defn render-patients [id]
;;   (println "id" id "type id" (type id))
;;   [:p id])

;; (defn get-patients []
;;   (go (let [response (<! (http/get "http://localhost:3000/get"))
;;             patients (get-in response [:body :result])
;;             first-patient (first patients)
;;             id (get-in first-patient [:id])]
;;         (render-patients id))))

;; (defn start []
;;   (let [id "Hi"]
;;     [render-patients id]))

(defn patients
  []
  (let [patient (r/atom nil)]

    (r/create-class                 
     {:display-name  "patients"      

      :component-did-mount              
      (fn [this]
        (go (let [response (<! (http/get "http://localhost:3000/get"))
                  result (get-in response [:body :result])]
              (swap! patient result)
              (println "patient" patient))))




      :reagent-render        
      (fn []           
        [:div (str @patient)])})))

;; (r/render
;;  [patients]         
;;  (.-body js/document))

(defn homepage []
  [:div
   [:h1 "Welcome"]
   [patients]])

(defn init! []
  (d/render [homepage] (.getElementById js/document "app")))


