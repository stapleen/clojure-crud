(ns frontend.components.input)

(defn component
  [type label value func]
  [:div {:class "div-input"}
   [:span (str label)]
   [:div
    [:input {:type type :value value :on-change func :class "input"}]]
   ])