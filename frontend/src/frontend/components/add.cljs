(ns frontend.components.patient.add
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [frontend.config :as config]
   [frontend.components.input :as input]
   [frontend.components.select :as select]
   [frontend.components.button :as button]
   [frontend.components.picker :as picker]
   [reagent-material-ui.core.paper :refer [paper]]))

(defn component
  []
  (let [full-name (r/atom "")
        gender (r/atom "М")
        date-of-birth (r/atom "")]

    (r/create-class
     {:display-name  "patient-add"

      :reagent-render
      (fn []
        [paper
         [:div {:class "form"}
          [:p "Добавление пациента"]
          [input/component "outlined" @full-name "ФИО" #(reset! full-name (-> % .-target .-value)) false]
          [select/component @gender #(reset! gender (-> % .-target .-value)) "Пол"]
          [picker/component "outlined" @date-of-birth "Дата рожедния" #(reset! date-of-birth (-> % .-target .-value))]
          [:div {:class "buttons"}
           [button/component
            "outlined"
            (fn []
              (go (let [response (<! (http/post (str config/url "/add")  {:json-params {:full_name @full-name :gender @gender :date_of_birth @date-of-birth}}))
                        success (get-in response [:body :success])
                        result (if (zero? success) (get-in response [:body :error]) (get-in response [:body :result]))]
                    (js/alert result)))
              (set! (.. js/document -location -href) "#/"))
            "Добавить"]
           [button/component
            "outlined"
            (fn [] (set! (.. js/document -location -href) "#/"))
            "Отмена"]]]])})))