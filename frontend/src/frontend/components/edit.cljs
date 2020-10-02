(ns frontend.components.patient.edit
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [frontend.components.input :as input]
   [frontend.components.select :as select]))

(defn component
  [id]
  (let [full-name (r/atom nil)
        gender (r/atom nil)
        date-of-birth (r/atom nil)]

  (r/create-class
   {:display-name  "patient-edit"

    :component-did-mount
    (fn [this]

      (go (let [response (<! (http/post "http://localhost:3000/get/patient" {:json-params {:id id}}))
                result (get-in response [:body :result])
                patient (first result)
                patient-name (get-in patient [:full_name])
                patient-gender (get-in patient [:gender])
                patient-birth (get-in patient [:date_of_birth])]

            (reset! full-name patient-name)
            (reset! gender patient-gender)
            (reset! date-of-birth (subs patient-birth 0 10)))))

    :reagent-render
    (fn [id]

      [:div
       [:p "Редактирование"]

       [input/component "text" "ФИО" @full-name #(reset! full-name (-> % .-target .-value))]
       [select/component "Пол"  @gender #(reset! gender (-> % .-target .-value))]
       [input/component "date" "Дата рождения" @date-of-birth #(reset! date-of-birth (-> % .-target .-value))]

       [:div
        [:input {:type "button"
                 :value "Сохранить"
                 :on-click (fn []
                             (go (let [response (<! (http/post "http://localhost:3000/update"  {:json-params {:id id :full_name @full-name :gender @gender :date_of_birth @date-of-birth}}))
                                       success (get-in response [:body :success])
                                       result (if (zero? success) (get-in response [:body :error]) (get-in response [:body :result]))]
                                   (js/alert result)))
                             (set! (.. js/document -location -href) "#/"))}]
        [:input {:type "button" :value "Отмена" :on-click (fn [] (set! (.. js/document -location -href) "#/"))}]]])})))