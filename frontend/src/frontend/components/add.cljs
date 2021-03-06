(ns frontend.components.patient.add
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [frontend.config :as config]
   [frontend.routes :refer [home]]
   [frontend.components.input :as input]
   [frontend.components.select :as select]
   [frontend.components.button :as button]
   [frontend.components.picker :as picker]
   [reagent-material-ui.core.paper :refer [paper]]
   [frontend.components.snackbar :as snackbar]))

(defn component
  []
  (let [full-name (r/atom "")
        gender (r/atom "М")
        date-of-birth (r/atom "")
        severity (r/atom nil)
        message (r/atom nil)
        open? (r/atom false)]
        
    (letfn [(show-error-message [text]
                                ((reset! severity "error")
                                 (reset! open? true)
                                 (reset! message text)))
            (go-home []
                     (set! (.. js/document -location -href) (str "#" home)))]

      (r/create-class
       {:display-name  "patient-add"

        :reagent-render
        (fn []
          [paper
           [:div {:class "form"}
            [snackbar/component @open? (fn [] (reset! open? false)) @severity @message]
            [:p "Добавление пациента"]
            [input/component "outlined" @full-name "ФИО" #(reset! full-name (-> % .-target .-value)) false]
            [select/component @gender #(reset! gender (-> % .-target .-value)) "Пол"]
            [picker/component "outlined" @date-of-birth #(reset! date-of-birth (-> % .-target .-value))]
            [:div {:class "buttons"}
             [button/component
              "outlined"
              (fn []
                (go (let [response (<! (http/post (str config/url "/patient/add")
                                                  {:json-params
                                                   {:full_name @full-name
                                                    :gender @gender
                                                    :date_of_birth @date-of-birth}}))
                          status (get-in response [:status])]
                      (cond
                        (= status 500) (show-error-message "Ошибка сервера")
                        (= status 400) (show-error-message (get-in response [:body :error]))
                        (= status 200) (go-home)))))
              "Добавить"]
             [button/component
              "outlined"
              (fn [] (go-home))
              "Отмена"]]]])}))))