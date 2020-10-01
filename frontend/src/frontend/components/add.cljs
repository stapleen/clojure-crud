(ns frontend.components.patient.add
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [frontend.components.input :as input]
   [frontend.components.select :as select]))

(defn component
  []
  (let [full-name (r/atom "")
        gender (r/atom "")
        date-of-birth (r/atom "")]

    (r/create-class
     {:display-name  "patient-add"

      :reagent-render
      (fn []
        [:div
         [:h1 "Добавление пациента"]
         [input/component "text" "full-name " @full-name #(reset! full-name (-> % .-target .-value))]
         [select/component "gender " #(reset! gender (-> % .-target .-value))]
         [input/component "date" "date_of_birth " @date-of-birth #(reset! date-of-birth (-> % .-target .-value))]

         [:div
          [:input {:type "button"
                   :value "Добавить"
                   :on-click (fn []
                               (go (let [response (<! (http/post "http://localhost:3000/add"  {:json-params {:full_name @full-name :gender @gender :date_of_birth @date-of-birth}}))
                                         success (get-in response [:body :success])
                                         result (if (zero? success) (get-in response [:body :error]) (get-in response [:body :result]))]
                                     (println "response" result))))}]
          [:input {:type "button" :value "Отмена" :on-click (fn [] (set! (.. js/document -location -href) "#/"))}]]])})))