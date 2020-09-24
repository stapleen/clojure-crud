(ns frontend.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]))
  
(defn home-page []
  (go (let [p (<! (http/get "http://localhost:3000/get"))]
        (println "p" p))))

(defn init! []
  (d/render [home-page] (.getElementById js/document "app")))
