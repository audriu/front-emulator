(ns front-emulator.compiler-page
  (:require
    [re-frame.core :as rf]
    [cljs.js :refer [eval-str empty-state js-eval]]))

(def default-c [:svg {:style {:border     "1px solid"
                              :background "white"
                              :width      "150px"
                              :height     "150px"}}
                [:circle {:r 50 :cx 75 :cy 75 :fill "orange"}]
                [:circle {:r 25 :cx 100 :cy 100 :fill "green"}]])

(def default-code (pr-str default-c))

(defn valid-hiccup? [vec]
  (let [first-element (nth vec 0 nil)]
    (cond
      (not (vector? vec)) false
      (not (pos? (count vec))) false
      (string? first-element) false
      (not (reagent.impl.template/valid-tag? first-element)) false
      (not (every? true? (map valid-hiccup? (filter vector? vec)))) false
      :else true)))

(defn compilation []
  (let [source-string @(rf/subscribe [:source])]
    (eval-str (empty-state)
              (str "(ns cljs.user  (:refer-clojure :exclude [atom]) (:require [re-com.core :as re-com]))"
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
                  (if (valid-hiccup? value)
                    (do
                      (rf/dispatch [:delete-error-message])
                      (rf/dispatch [:set-result value]))
                    (rf/dispatch [:set-error "Your hiccup is invalid"])))))))

(defn compiler-page []
  (when-not @(rf/subscribe [:source])
    (rf/dispatch-sync [:set-source default-code]))
  [:span {:style {:display "flex"}}
   [:textarea
    {:rows      15
     :cols      50
     :value     @(rf/subscribe [:source])
     :on-change #(rf/dispatch [:set-source (-> % .-target .-value)])}]
   [:div#result-pane @(rf/subscribe [:result])]
   [:div#error-pane {:style {:color "red"}} @(rf/subscribe [:error])]
   [compilation]])