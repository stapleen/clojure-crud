(ns frontend.components.snackbar
  (:require
   [reagent-material-ui.core.snackbar :refer [snackbar]]
   [frontend.components.alert :as alert]))
(defn component
  [open func severity message]
  [snackbar {:open open
             :autoHideDuration 6000
             :on-close func
             :anchor-origin {:vertical "top"
                             :horizontal "center"}}
   [alert/component func severity message]])