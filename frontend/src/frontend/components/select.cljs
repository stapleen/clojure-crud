(ns frontend.components.select)

(defn component
  [label value func]
  [:div
   [:div (str label)]
   [:select {:on-change func :class "select"}
    [:option {:value "М" :selected (= value "М")} "М"]
    [:option {:value "Ж" :selected (= value "Ж")} "Ж"]]])