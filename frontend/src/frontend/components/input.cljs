(ns frontend.components.input
  (:require
   [reagent-material-ui.core.text-field :refer [text-field]]))

(defn component
  [variant value label func]
  [:div {:class "input"}
   [text-field
    {:variant variant
     :value value
     :label label
     :on-change func}]])