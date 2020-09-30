(ns frontend.input)

(defn component
  [type label value func]
  [:div
   [:span (str label)]
   [:input {:type type :value value :on-change func }]])