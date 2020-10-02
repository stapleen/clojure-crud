(ns frontend.components.input)

(defn component
  [type label value func]
  [:div
   [:div (str label) ]
   [:input {:type type :value value :on-change func :class "input"}]])