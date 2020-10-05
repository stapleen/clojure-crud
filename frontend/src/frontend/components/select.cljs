(ns frontend.components.select
  (:require
   [reagent-material-ui.core.text-field :refer [text-field]]
   [reagent-material-ui.core.menu-item :refer [menu-item]]))

(defn component
  [value func label]
  [:div
   [text-field
    {:value value
     :label label
     :on-change func
     :select true}
    [menu-item
     {:value "М"}
     "М"]
    [menu-item
     {:value "Ж"}
     "Ж"]]])