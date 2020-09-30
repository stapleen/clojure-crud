(ns frontend.input
  (:require
   [reagent.core :as r]))

(defn component
  [label value func]
  [:div
   [:span (str label)]
   [:input {:type "text" :value value :on-change func }]])