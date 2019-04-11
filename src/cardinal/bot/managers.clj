(ns cardinal.bot.managers
  (:require
   [fsm-clj.core :as fsm]
   [cardinal.interop.base :as b]
   [taoensso.timbre :as timbre
    :refer [log  trace  debug  info  warn  error  fatal  report
            logf tracef debugf infof warnf errorf fatalf reportf
            spy get-env]]
   [clj-time.local :as l]))

(defn make-mtemplate
  [template start-state]
  (partial fsm/fsm template start-state))

(defn make-model
  ([template start-state init-data & kvargs]
   (merge
    kvargs
    {:init-data init-data
     :mtemplate (make-mtemplate template start-state)}))
  ([mtemplate init-data & kvargs]
   (merge
    kvargs
    {:init-data init-data
     :mtemplate mtemplate})))

(defn make-micromanager
  [main-state model & kvargs]
  (let [init-data (:init-data model)
        mtemplate (:mtemplate model)
        data {:main-state main-state
              :event nil
              :chan (agent [])
              :data init-data}]
    (merge kvargs {:fsm (mtemplate data)})))

(defn make-macromanager
  [main-state model & kvargs]
  (let [init-data (:init-data model)
        mtemplate (:mtemplate model)
        data {:main-state main-state
              :event nil
              :chan (agent [])
              :data init-data}]
    (merge {:fsm (mtemplate data)}
           kvargs)))

(defn send-message
  [manager msg]
  (update manager :fsm
          fsm/update-data
          #(do
             (send (:chan %) (fn [lst] (conj lst msg)))
             %)))

(defn set-main-state
  [manager main-state]
  (fsm/update-data
   (:fsm manager)
   #(assoc % :main-state main-state)))

(defn set-event
  [manager event]
  (fsm/update-data
   (:fsm manager)
   #(assoc % :event event)))

(defn get-event-action-key
  [event]
  (keyword (str "on-" (name event))))

(defn update-manager
  ([manager event]
   (-> manager
       (set-event event)
       (update :fsm
               fsm/update-data
               (get manager
                    (get-event-action-key
                     event)))
       (update :fsm
               fsm/update-fsm))))

(defn update-managers
  ([managers & {:keys [async] :or {async false}}]
   (update-managers managers nil :async async))
  ([managers event & {:keys [async] :or {async false}}]
   (let [maper (if async
                 pmap
                 map)]
     (apply vector
            (maper #(update-manager % event)
                   managers)))))
