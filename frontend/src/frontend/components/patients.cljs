(ns frontend.components.patients
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]))

(defn component
  []
  (let [patients (r/atom nil)
        loading (r/atom true)]

    (r/create-class
     {:display-name  "patients"

      :component-did-mount
      (fn [this]
        (go (let [response (<! (http/get "http://localhost:3000/get"))
                  result (get-in response [:body :result])]
              (reset! patients result)
              (reset! loading false))))

      :reagent-render
      (fn []
        (let [patients-list
              (map #(let [id (get-in % [:id])
                          full-name (get-in % [:full_name])
                          gender (get-in % [:gender])
                          date_of_birth (get-in % [:date_of_birth])]

                      [:tr {:key id}
                       [:td full-name]
                       [:td gender]
                       [:td date_of_birth]
                       [:td [:input {:type "button"
                                     :value "Редактировать"
                                     :on-click (fn []
                                                 (set! (.. js/document -location -href) (str "#/edit/" id)))}]]
                       [:td [:input {:type "button"
                                     :value "Удалить"
                                     :on-click
                                     (fn []
                                       (go (let [response (<! (http/post "http://localhost:3000/delete"
                                                                         {:json-params {:id id}}))
                                                 success (get-in response [:body :success])]
                                             (if (zero? success)
                                               (println (get-in response [:body :error]))
                                               (reset! patients
                                                       (filterv
                                                        (fn [x] (not= (get-in x [:id]) id)) @patients))))))}]]])
                   @patients)]
          
          (if (true? @loading) [:p "Загрузка"]
              (if (empty? patients-list)
                [:div
                 [:input {:type "button"
                          :value "Добавить пациента"
                          :on-click (fn [] (set! (.. js/document -location -href) "#/new"))}]
                 [:p "Список пациентов пуст"]]
                [:div
                 [:input {:type "button"
                          :value "Добавить пациента"
                          :on-click (fn [] (set! (.. js/document -location -href) "#/new"))}]
                 [:table
                  [:tr
                   [:th "Полное имя"]
                   [:th "Пол"]
                   [:th "Дата рождения"]
                   [:th ""]
                   [:th ""]]
                  patients-list]]))))})))