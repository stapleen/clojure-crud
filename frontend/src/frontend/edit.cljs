(ns frontend.patient.edit
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]))

(defn component
  []
  (let [id (r/atom 11)
        full-name (r/atom "Vadim krikoten")
        gender (r/atom "M")
        date_of_birth (r/atom "2015-10-09T21:00:00Z")]

    (r/create-class
     {:display-name  "patient-edit"

      :reagent-render
      (fn []
        [:div
         [:h1 "Редактирование"]
         [:div
          [:span "full-name "]
          [:input {:type "text" :value @full-name :on-change #(reset! full-name (-> % .-target .-value))}]]

         [:div
          [:span "gender "]
          [:input {:type "text" :value @gender :on-change #(reset! gender (-> % .-target .-value))}]]

         [:div
          [:span "date_of_birth "]
          [:input {:type "text" :value @date_of_birth :on-change #(reset! date_of_birth (-> % .-target .-value))}]]

         [:div
          [:input {:type "button" :value "Сохранить" :on-click (fn [] (println "name" full-name "gender" gender "date_of_birth" date_of_birth))}]
          [:input {:type "button" :value "Отмена"}]]])})))