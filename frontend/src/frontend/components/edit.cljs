(ns frontend.components.patient.edit
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [moment :as moment]
   [reagent-material-ui.core.circular-progress :refer [circular-progress]]
   [frontend.config :as config]
   [frontend.routes :refer [home]]
   [frontend.components.input :as input]
   [frontend.components.select :as select]
   [frontend.components.button :as button]
   [frontend.components.picker :as picker]
   [reagent-material-ui.core.paper :refer [paper]]
   [frontend.components.snackbar :as snackbar]))

(defn go-home []
  (set! (.. js/document -location -href) (str "#" home)))

(defn component
  [id]
  (let [full-name (r/atom nil)
        gender (r/atom nil)
        date-of-birth (r/atom nil)
        loading? (r/atom true)
        severity (r/atom nil)
        message (r/atom nil)
        open? (r/atom false)]

    (letfn [(show-error-message [text]
              ((reset! severity "error")
               (reset! open? true)
               (reset! message text)))
            (go-home []
              (set! (.. js/document -location -href) "#/"))]

      (r/create-class
       {:display-name  "patient-edit"

        :component-did-mount
        (fn [this]
          (go (let [response (<! (http/get (str config/url "/patient") {:query-params {:id id}}))
                    result (get-in response [:body :result])
                    patient (first result)
                    patient-name (get-in patient [:full_name])
                    patient-gender (get-in patient [:gender])
                    patient-birth (get-in patient [:date_of_birth])]

                (reset! loading? false)
                (reset! full-name patient-name)
                (reset! gender patient-gender)
                (reset! date-of-birth (.format  (moment. patient-birth) "YYYY-MM-DD")))))

        :reagent-render
        (fn [id]
          (if (true? @loading?) [circular-progress {:color "secondary"}]
              (if (nil? @full-name)
                [:p "Пациент не найден"]
                [paper
                 [:div {:class "form"}
                  [snackbar/component @open? (fn [] (reset! open? false)) @severity @message]
                  [:p "Редактирование"]
                  [input/component "outlined" @full-name "ФИО" #(reset! full-name (-> % .-target .-value)) false]
                  [select/component @gender #(reset! gender (-> % .-target .-value)) "Пол"]
                  [picker/component "outlined" @date-of-birth #(reset! date-of-birth (-> % .-target .-value))]
                  [:div {:class "buttons"}
                   [button/component
                    "outlined"
                    (fn []
                      (go (let [response (<! (http/post (str config/url "/patient/update")
                                                        {:json-params {:id id
                                                                       :full_name @full-name
                                                                       :gender @gender
                                                                       :date_of_birth @date-of-birth}}))
                                status (get-in response [:status])]
                            (cond
                              (= status 500) (show-error-message "Ошибка сервера")
                              (= status 400) (show-error-message (get-in response [:body :error]))
                              (= status 200) (go-home)))))
                    "Сохранить"]
                   [button/component
                    "outlined"
                    (fn [] (go-home))
                    "Отмена"]]]])))}))))