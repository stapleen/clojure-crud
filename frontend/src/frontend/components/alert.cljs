(ns frontend.components.alert
  (:require
   [reagent-material-ui.lab.alert :refer [alert]]))

(defn component
  [func severity message]
  [alert {:severity severity
          :on-close func}
   (str message)])