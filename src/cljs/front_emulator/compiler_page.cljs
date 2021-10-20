(ns front-emulator.compiler-page
  (:require
    [re-frame.core :as rf]
    [cljs.js :refer [eval-str empty-state js-eval]]))

(def default-code [:svg {:style {:border     "1px solid"
                                 :background "white"
                                 :width      "350px"
                                 :height     "350px"}}
                   [:circle {:r 50 :cx 75 :cy 75 :fill "orange"}]
                   [:circle {:r 25 :cx 100 :cy 100 :fill "green"}]])

(defn compilation []
  (let [source-string @(rf/subscribe [:source])]
    (eval-str (empty-state)
              (str "(ns cljs.user (:refer-clojure :exclude [atom])(:require [re-com.core :as re-com]))"
                   (or (not-empty source-string)
                       "[:div]"))
              'user-code
              {:ns            'cljs.user
               :eval          js-eval
               :static-fns    true
               :def-emits-var false
               :load          (fn [_ cb]
                                (cb {:lang :clj :source ""}))
               :context       :statement}
              (fn [{:keys [error value]}]
                (if error
                  (rf/dispatch [:set-error error])
                  (do
                    (rf/dispatch [:set-error nil])
                    (rf/dispatch [:set-result value])))))))

(defn compiler-page []
  (when-not @(rf/subscribe [:source])
    (rf/dispatch-sync [:set-source (pr-str default-code)]))
  [:span {:style {:display "flex"}}
   [:textarea
    {:rows      15
     :cols      50
     :value     @(rf/subscribe [:source])
     :on-change #(rf/dispatch [:set-source (-> % .-target .-value)])}]
   [:div#result-pane @(rf/subscribe [:result])]
   [:div#error-pane {:style {:color "red"}} @(rf/subscribe [:error])]
   (compilation)])