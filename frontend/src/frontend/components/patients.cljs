(ns frontend.components.patients
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [moment :as moment]
   [reagent-material-ui.core.circular-progress :refer [circular-progress]]
   [frontend.config :as config]
   [reagent-material-ui.core.table :refer [table]]
   [reagent-material-ui.core.table-body :refer [table-body]]
   [reagent-material-ui.core.table-cell :refer [table-cell]]
   [reagent-material-ui.core.table-container :refer [table-container]]
   [reagent-material-ui.core.table-head :refer [table-head]]
   [reagent-material-ui.core.table-row :refer [table-row]]
   [reagent-material-ui.core.paper :refer [paper]]
   [frontend.components.snackbar :as snackbar]
   [frontend.components.icon-btn :as icon-btn]
   [reagent-material-ui.icons.delete-forever :refer [delete-forever]]
   [reagent-material-ui.icons.edit :refer [edit]]
   [reagent-material-ui.icons.add :refer [add]]))

(defn component
  []
  (let [patients (r/atom nil)
        error (r/atom nil)
        severity (r/atom nil)
        message (r/atom nil)
        loading? (r/atom true)
        open? (r/atom false)]

    (letfn [(show-error-message [text]
              ((reset! severity "error")
               (reset! open? true)
               (reset! message text)))

            (success-response [text id]
              ((reset! severity "success")
               (reset! message text)
               (reset! patients
                       (filterv
                        (fn [x] (not= (get-in x [:id]) id)) @patients))
               (reset! open? true)))

            (render-patients [patients]
              (map #(let [id (get-in % [:id])
                          full-name (get-in % [:full_name])
                          gender (get-in % [:gender])
                          date_of_birth (get-in % [:date_of_birth])
                          date-formated (.format  (moment. date_of_birth) "DD.MM.YYYY")]
                      [table-row {:key id}
                       [table-cell full-name]
                       [table-cell gender]
                       [table-cell date-formated]
                       [table-cell
                        [:div {:class "tableButtons"}
                         [snackbar/component @open? (fn [] (reset! open? false)) @severity @message]
                         [icon-btn/component [edit]
                          (fn [] (set! (.. js/document -location -href) (str "#/edit/" id)))]
                         [icon-btn/component [delete-forever]
                          (fn []
                            (go (let [response (<! (http/post (str config/url "/delete")
                                                              {:json-params {:id id}}))
                                      status (get-in response [:status])]
                                  (cond
                                    (= status 500) (show-error-message "Ошибка сервера")
                                    (= status 200) (if (zero? (get-in response [:body :success]))
                                                     (show-error-message (get-in response [:body :error]))
                                                     (success-response (get-in response [:body :result]) id))))))]]]])
                   patients))

            (render-table [patients-list]
              [paper
               [icon-btn/component [add]
                (fn [] (set! (.. js/document -location -href) "#/new"))]
               [table-container
                [table
                 [table-head
                  [table-row
                   [table-cell "Полное имя"]
                   [table-cell "Пол"]
                   [table-cell "Дата рождения"]
                   [table-cell ""]]]
                 [table-body
                  patients-list]]]])]

      (r/create-class
       {:display-name  "patients"

        :component-did-mount
        (fn [this]
          (go (let [response (<! (http/get (str config/url "/get")))
                    status (get-in response [:status])]
                (cond
                  (= status 200) (reset! patients (get-in response [:body :result]))
                  (= status 500) (reset! error "Ошибка сервера"))
                (reset! loading? false))))

        :reagent-render
        (fn []
          (println "patients" @patients "error" @error "loading" @loading?)

          (if (true? @loading?) [circular-progress {:color "secondary"}]
              (if (nil? @patients) [:p "Ошибка сервера"]
                  (let [patients-list (render-patients @patients)]
                    (if (empty? patients-list)
                      [:div
                       [icon-btn/component [add]
                        (fn [] (set! (.. js/document -location -href) "#/new"))]
                       [:p "Список пациентов пуст"]]
                      (render-table patients-list))))))}))))