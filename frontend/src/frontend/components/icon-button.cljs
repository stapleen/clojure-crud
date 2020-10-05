(ns frontend.components.icon-btn
  (:require
   [reagent-material-ui.core.icon-button :refer [icon-button]]
   ))

(defn component
  [icon func]
  [:div {:on-click func}
   [icon-button
    icon]])