(ns front-emulator.core
  (:require
    [reagent.dom :as rdom]
    [re-frame.core :as re-frame]
    [front-emulator.compiler-page :refer [compiler-page]]
    [front-emulator.events :as events]))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [compiler-page] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (mount-root))
