(ns frontend.components.button
  (:require
   [reagent-material-ui.core.button :refer [button]]))

(defn component
  [variant func label]
  [button
   {:class "button"
    :variant variant
    :on-click func}
   label])