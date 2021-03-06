(ns frontend.components.picker
  (:require
   [reagent-material-ui.core.text-field :refer [text-field]]))

(defn component
  [variant value func]
  [:div {:class "input"}
   [text-field
    {:variant variant
     :value value
     :type "date"
     :on-change func}]])