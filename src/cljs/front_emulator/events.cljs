(ns front-emulator.events
  (:require
    [re-frame.core :as re-frame]
    [day8.re-frame.tracing :refer-macros [fn-traced]]))

(re-frame/reg-event-db
  ::initialize-db
  (fn-traced [_ _] {}))

;;;;Compiler stuff
(re-frame/reg-sub
  :source
  (fn [db]
    (:source db)))

(re-frame/reg-sub
  :result
  (fn [db]
    (:result db)))

(re-frame/reg-sub
  :error
  (fn [db]
    (:error db)))

(re-frame/reg-event-db
  :set-source
  (fn [state [_ new-text]]
    (assoc-in state [:source] new-text)))

(re-frame/reg-event-db
  :set-result
  (fn [state [_ new-result]]
    (assoc-in state [:result] new-result)))

(re-frame/reg-event-db
  :delete-error-message
  (fn [state _]
    (dissoc state :error)))

(re-frame/reg-event-db
  :set-error
  (fn [state [_ new-error]]
    (assoc-in state [:error] new-error)))
