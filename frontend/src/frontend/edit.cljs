(ns frontend.patient.edit
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [frontend.input :as input]))

(defn component
  []
  (let [id (r/atom 11)
        full-name (r/atom "Vadim krikoten")
        gender (r/atom "M")
        date_of_birth (r/atom "2015-10-09")]

    (r/create-class
     {:display-name  "patient-edit"

      :reagent-render
      (fn []
        [:div
         [:h1 "Редактирование"]

         [input/component "full-name " @full-name #(reset! full-name (-> % .-target .-value))]
         [input/component "gender " @gender #(reset! gender (-> % .-target .-value))]
         [input/component "date_of_birth " @date_of_birth #(reset! date_of_birth (-> % .-target .-value))]

         [:div
          [:input {:type "button"
                   :value "Сохранить"
                   :on-click (fn []
                               (go (let [response (<! (http/post "http://localhost:3000/update"  {:json-params {:id @id :full_name @full-name :gender @gender :date_of_birth @date_of_birth}}))
                                         success (get-in response [:body :success])
                                         result (if (zero? success) (get-in response [:body :error]) (get-in response [:body :result]))]
                                     (println "response" result))))}]
          [:input {:type "button" :value "Отмена"}]]])})))