(ns frontend.patients
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]))


(defn component
  []
  (let [patient (r/atom nil)]

    (r/create-class
     {:display-name  "patients"

      :component-did-mount
      (fn [this]
        (go (let [response (<! (http/get "http://localhost:3000/get"))
                  result (get-in response [:body :result])]
            ;;   (reset! patient result)
            ;;   (println "patient" patient))))
              (swap! patient result))))

      :reagent-render
      (fn []

        ;; (mapv #([:div "HI"]) @patient)

        (let [id (get-in @patient [:id])
              full-name (get-in @patient [:full_name])
              gender (get-in @patient [:gender])
              date_of_birth (get-in @patient [:date_of_birth])]

          [:div
           [:input {:type "button" :value "Добавить пациента" :on-click (fn [] (set! (.. js/document -location -href) "#/new"))}]

           [:div
            [:input {:type "button" :value "Редактировать" :on-click (fn [] (set! (.. js/document -location -href) "#/edit/10"))}]
            [:input {:type "button"
                     :value "Удалить"
                     :on-click (fn []
                                 (go (let [response (<! (http/post "http://localhost:3000/delete"  {:json-params {:id id}}))
                                           success (get-in response [:body :success])
                                           result (if (zero? success) (get-in response [:body :error]) (get-in response [:body :result]))]
                                       (println "response" result))))}]]

           [:p "full-name: " full-name]
           [:p "gender: " gender]
           [:p "date_of_birth: " date_of_birth]]))})))