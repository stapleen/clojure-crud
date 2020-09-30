(ns frontend.select)

(defn component
  [label func]
  [:div
   [:span (str label)]
   [:select {:on-change func}
    [:option {:value "М"} "М"]
    [:option {:value "Ж"} "Ж"]]])