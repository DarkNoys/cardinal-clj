(ns cardinal.bot.managers
  (:require
   [fsm-clj.fsm :as fsm]
   [cardinal.interop.base :as b]
   [taoensso.timbre :as timbre
    :refer [log  trace  debug  info  warn  error  fatal  report
            logf tracef debugf infof warnf errorf fatalf reportf
            spy get-env]]
   [clj-time.local :as l]))

(defn make-mtemplate
  [template start-state]
  (partial fsm/make-fsm template start-state))

(defn make-model
  ([template start-state init-data]
   {:init-data init-data
    :mtemplate (make-mtemplate template start-state)})
  ([mtemplate init-data]
   {:init-data init-data
    :mtemplate mtemplate}))

(defn make-micromanager
  [main-state sub-units model & {:keys [discription]}]
  (let [init-data (:init-data model)
        mtemplate (:mtemplate model)
        data {:main-state main-state
              :sub-units sub-units
              :chan (agent [])
              :data init-data}]
    {:discription discription
     :fsm (mtemplate data)}))

(defn make-macromanager
  [main-state sub-units sub-managers model & {:keys [discription]}]
  (let [init-data (:init-data model)
        mtemplate (:mtemplate model)
        data {:main-state main-state
              :sub-managers sub-managers
              :sub-units sub-units
              :chan (agent [])
              :data init-data}]
    {:discription discription
     :fsm (mtemplate data)}))

(defn send-message
  [manager msg]
  (update manager :fsm fsm/update-data #(do
                                          (send (:chan %) (fn [lst] (conj lst msg)))
                                          %)))

(defn set-main-state
  [manager main-state]
  (fsm/update-data (:fsm manager)
                   #(assoc % :main-state main-state)))

(defn update-manager
  [manager]
  (update manager :fsm fsm/update-fsm))

(defn update-managers
  [managers & {:keys [async] :or {async false}}]
  (let [maper (if async
                pmap
                map)]
    (apply vector (maper update-manager managers))))
