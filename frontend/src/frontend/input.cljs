(ns frontend.input)

(defn component
  [label value func]
  [:div
   [:span (str label)]
   [:input {:type "text" :value value :on-change func }]])