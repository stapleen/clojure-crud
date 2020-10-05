(ns frontend.components.patient.edit
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [reagent-material-ui.core.circular-progress :refer [circular-progress]]
   [frontend.config :as config]
   [frontend.components.input :as input]
   [frontend.components.select :as select]
   [frontend.components.button :as button]
   [frontend.components.picker :as picker]))

(defn component
  [id]
  (let [full-name (r/atom nil)
        gender (r/atom nil)
        date-of-birth (r/atom nil)
        loading? (r/atom true)]

  (r/create-class
   {:display-name  "patient-edit"

    :component-did-mount
    (fn [this]
      (go (let [response (<! (http/post (str config/url "/get/patient") {:json-params {:id id}}))
                result (get-in response [:body :result])
                patient (first result)
                patient-name (get-in patient [:full_name])
                patient-gender (get-in patient [:gender])
                patient-birth (get-in patient [:date_of_birth])]

            (reset! loading? false)
            (reset! full-name patient-name)
            (reset! gender patient-gender)
            (reset! date-of-birth (subs patient-birth 0 10)))))

    :reagent-render
    (fn [id]
      (if (true? @loading?) [circular-progress {:color "secondary"}]
          (if (nil? @full-name)
            [:p "Пациент не найден"]
            [:div
             [:p "Редактирование"]
             [input/component "outlined" @full-name "ФИО" #(reset! full-name (-> % .-target .-value)) false]
             [select/component @gender #(reset! gender (-> % .-target .-value)) "Пол"]
             [picker/component "outlined" @date-of-birth "Дата рожедния" #(reset! date-of-birth (-> % .-target .-value))]
             [:div
              [button/component
               "outlined"
               (fn []
                 (go (let [response (<! (http/post (str config/url "/update")  {:json-params {:id id :full_name @full-name :gender @gender :date_of_birth @date-of-birth}}))
                           success (get-in response [:body :success])
                           result (if (zero? success) (get-in response [:body :error]) (get-in response [:body :result]))]
                       (js/alert result)))
                 (set! (.. js/document -location -href) "#/"))
               "Сохранить"]
              [button/component
               "outlined"
               (fn [] (set! (.. js/document -location -href) "#/"))
               "Отмена"]]])))})))