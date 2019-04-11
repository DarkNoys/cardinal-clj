(ns cardinal.bot.models.base-manager
  (:require
   [taoensso.timbre :as timbre
    :refer [log  trace  debug  info  warn  error  fatal  report
            logf tracef debugf infof warnf errorf fatalf reportf
            spy get-env]] [fsm-clj.fsm :as fsm]
   [cardinal.bot.models.worker :as worker]
   [cardinal.bot.managers :as m]
   [cardinal.interop.base :as b]))

(defn send-mine-ore
  [manager unit]
  (m/send-message manager
                  {:data {:unit (:unit unit)}
                   :type :mine-ore}))

(defn on-unit-create
  [{:keys [data main-state] :as state} unit]
  (if (and
       (not (nil? (:worker-manager data)))
       (= (b/get-player unit)
          (:player @main-state))
       (b/is-worker? (b/get-type unit)))
    (do (send-mine-ore
         (:worker-manager data)
         unit)
        (update state [:data :workers]
                conj {:unit unit :task nil}))
    state))

;;;;;;;;;;;;;;;
;; Init data ;;
;;;;;;;;;;;;;;;

(defn init-data
  []
  {:workers []
   :build {:start-build false
           :worker nil}
   :command-center nil
   :managers {}})

;;;;;;;;;;;;;;;;
;; Start state;;
;;;;;;;;;;;;;;;;

(def start-state
  :s1)

;;;;;;;;;;;;;
;; Actions ;;
;;;;;;;;;;;;;

;; S1

(defn s1-action
  [{:keys [data main-state] :as state}]
  (if (empty? (:workers data))
    (let [player (:player @main-state)
          units (b/get-units player)
          nworkers (filter #(b/is-worker? (b/get-type %)) units)
          command-center (first
                          (filter
                           #(= (b/get-type %)
                               bwapi.UnitType$Terran_Command_Center)
                           units))
          worker-with-task (map #({:unit % :task nil}) nworkers)]
      (-> state
          (update-in [:data :command-center]
                     command-center)
          (update-in [:data :workers]
                     worker-with-task)))
    state))

(defn s1-valid-s2
  [{:keys [data] :as state}]
  (seq (:workers data)))

;; S2

(defn s2-action
  [{:keys [data main-state] :as state}]
  (if (nil? (get-in data
                    [:managers
                     :worker-manager]))
    (update state :data
            assoc-in [:managers
                       :worker-manager]
                     (m/make-micromanager main-state
                                          []
                                          worker/model))
    state))

(defn s2-valid-s3
  [{:keys [data main-state] :as state}]
  (not 
    (nil? 
      (get-in data
              [:managers
               :worker-manager]))))

;; S3

(defn s3-action
  [{:keys [data main-state] :as state}]
  (let [worker-manager (get-in data
                               [:managers
                                :worker-manager])
        workers (:workers data)]
    (if (seq (filter #(nil? (:task %)) workers))
      (do
        (doseq [worker workers]
          (send-mine-ore worker-manager (:unit worker)))
        (update-in state [:data :workers]
                   (map #(assoc % :task :mine-ore))))
      state)))

(defn s3-valid-s4
  [{:keys [data main-state] :as state}]
  (let [workers (:workers data)]
    (empty? 
      (filter 
        #(nil? (:task %)) 
        workers))))

;; S4

(defn s4-action
  [{:keys [data main-state] :as state}]
  (let [command-center (:command-center data)]
    (when (and (not (b/is-training? command-center))
               (b/can-train? command-center
                                  bwapi.UnitType$Terran_SCV))
      (b/train command-center
               bwapi.UnitType$Terran_SCV))
    state))

(defn s4-valid-s5-1-1
  [{:keys [data main-state] :as state}]
  (= (count (:workers data)) 8))

(defn s4-valid-s5-2-1
  [{:keys [data main-state] :as state}]
  (= (count (:workers data)) 10))

(defn s4-valid-s5-3-1
  [{:keys [data main-state] :as state}]
  (= (count (:workers data)) 12))

(defn s4-valid-s5-4-1
  [{:keys [data main-state] :as state}]
  (= (count (:workers data)) 14))

(defn s4-valid-s6
  [{:keys [data main-state] :as state}]
  (= (count (:workers data)) 15))

(defn s4-valid-s8
  [{:keys [data main-state] :as state}]
  (= (count (:workers data)) 16))

;; S5

(defn s5-in-action
  [{:keys [data main-state] :as state}]
  (-> state
      (assoc-in
       [:data :build :worker]
       nil)
      (assoc-in
       [:data :build :start-build]
       false)))

(defn s5-valid-s4
  [{:keys [data main-state] :as state}]
  (get-in data
          [:build :start-build]))

;; S5.1.1

(defn s5-1-1-action
  [{:keys [data main-state] :as state}]
  (if (nil? (get-in data [:build :worker]))
    (let [miners ()]
      (update-in))
    state))

(defn s5-1-1-valid-s5-1-2
  [{:keys [data main-state] :as state}]
  false)

;; S5.1.2

(defn s5-1-2-action
  [{:keys [data main-state] :as state}]
  state)

;; S5.2.1

(defn s5-2-1-action
  [{:keys [data main-state] :as state}]
  state)

(defn s5-2-1-valid-s5-2-2
  [{:keys [data main-state] :as state}]
  false)

;; S5.2.2

(defn s5-2-2-action
  [{:keys [data main-state] :as state}]
  state)

;; S5.3.1

(defn s5-3-1-action
  [{:keys [data main-state] :as state}]
  state)

(defn s5-3-1-valid-s5-3-2
  [{:keys [data main-state] :as state}]
  false)

;; S5.3.2

(defn s5-3-2-action
  [{:keys [data main-state] :as state}]
  state)

;; S5.4.1

(defn s5-4-1-action
  [{:keys [data main-state] :as state}]
  state)

(defn s5-4-1-valid-s5-4-2
  [{:keys [data main-state] :as state}]
  false)

;; S5.4.2

(defn s5-4-2-action
  [{:keys [data main-state] :as state}]
  state)

;; S6

(defn s6-action
  [{:keys [data main-state] :as state}]
  state)

(defn s6-valid-s7
  [{:keys [data main-state] :as state}]
  false)

;; S7

(defn s7-action
  [{:keys [data main-state] :as state}]
  state)

(defn s7-valid-s4
  [{:keys [data main-state] :as state}]
  false)

;; S6

(defn s8-action
  [{:keys [data main-state] :as state}]
  state)

(defn s8-valid-s9
  [{:keys [data main-state] :as state}]
  false)

;; S9

(defn s9-action
  [{:keys [data main-state] :as state}]
  state)

(defn s9-valid-s8
  [{:keys [data main-state] :as state}]
  false)

(defn s9-valid-s10
  [{:keys [data main-state] :as state}]
  false)

;; S10

(defn s10-action
  [{:keys [data main-state] :as state}]
  state)

(defn s10-valid-s11
  [{:keys [data main-state] :as state}]
  false)

;; S11

(defn s11-action
  [{:keys [data main-state] :as state}]
  state)

(defn s11-valid-s12
  [{:keys [data main-state] :as state}]
  false)

(defn s11-valid-s13
  [{:keys [data main-state] :as state}]
  false)

;; S12

(defn s12-action
  [{:keys [data main-state] :as state}]
  state)

(defn s12-valid-s11
  [{:keys [data main-state] :as state}]
  false)

;; S13

(defn s13-action
  [{:keys [data main-state] :as state}]
  state)

(defn s13-valid-s11
  [{:keys [data main-state] :as state}]
  false)

;;;;;;;;;;;;;;
;; Template ;;
;;;;;;;;;;;;;;

(def template
  {:s1 {:discription "Получить список рабочих и командный центр"
        :action (fn [state]
                  (s1-action state))
        :edges [{:state :s2
                 :discription "Получен список рабочих"
                 :valid? (fn [state]
                           (s1-valid-s2 state))}]
        :type :normal}
   :s2 {:discription "Создать WorkerManager"
        :action (fn [state]
                  (s1-action state))
        :edges [{:state :s3
                 :discription "Создан WorkerManager"
                 :valid? (fn [state]
                           (s2-valid-s3 state))}]
        :type :normal}
   :s3 {:discription "Передать WorkerManager рабочих на добычу руды"
        :action (fn [state]
                  (s1-action state))
        :edges [{:state :s4
                 :discription "Переданы"
                 :valid? (fn [state]
                           (s3-valid-s4 state))}]
        :type :normal}
   :s4 {:discription "Строить рабочих"
        :edges [{:state :s5.1.1
                 :discription "8 рабочих"
                 :valid? (fn [state]
                           (s4-valid-s5-1-1 state))}
                {:state :s5.2.1
                 :discription "10 рабочих"
                 :valid? (fn [state]
                           (s4-valid-s5-2-1 state))}
                {:state :s5.3.1
                 :discription "12 рабочих"
                 :valid? (fn [state]
                           (s4-valid-s5-3-1 state))}
                {:state :s5.4.1
                 :discription "14 рабочих"
                 :valid? (fn [state]
                           (s4-valid-s5-4-1 state))}
                {:state :s6
                 :discription "15 рабочих"
                 :valid? (fn [state]
                           (s4-valid-s6 state))}
                {:state :s8
                 :discription "16 рабочих"
                 :valid? (fn [state]
                           (s4-valid-s8 state))}]
        :type :normal}
   :s5 {:discription "Постройка"
        :in-action (fn [state]
                     (s5-in-action state))
        :edges [{:state :s4
                 :discription "Строительство начато"
                 :valid? (fn [state]
                           (s5-valid-s4 state))}]
        :start-state :s5.1.1
        :type :heir}
   :s5.1.1 {:discription "Выбрать рабочего"
            :action (fn [state]
                      (s5-1-1-action state))
            :edges [{:state :s5.1.2
                     :discription "Выбран"
                     :valid? (fn [state]
                               (s5-1-1-valid-s5-1-2 state))}]
            :parent :s5
            :type :normal}
   :s5.1.2 {:discription "Отправить выбранного рабочего WorkerManager на постройку Депо"
            :action (fn [state]
                      (s5-1-2-action state))
            :edges []
            :parent :s5
            :type :normal}
   :s5.2.1 {:discription "Выбрать рабочего"
            :action (fn [state]
                      (s5-2-1-action state))
            :edges [{:state :s5.2.2
                     :discription "Выбран"
                     :valid? (fn [state]
                               (s5-2-1-valid-s5-2-2 state))}]
            :parent :s5
            :type :normal}
   :s5.2.2 {:discription "Отправить выбранного рабочего WorkerManager на постройку Казармы"
            :action (fn [state]
                      (s5-2-2-action state))
            :edges []
            :parent :s5
            :type :normal}
   :s5.3.1 {:discription "Выбрать рабочего"
            :action (fn [state]
                      (s5-3-1-action state))
            :edges [{:state :s5.3.2
                     :discription "Выбран"
                     :valid? (fn [state]
                               (s5-3-1-valid-s5-3-2 state))}]
            :parent :s5
            :type :normal}
   :s5.3.2 {:discription "Отправить выбранного рабочего WorkerManager на постройку Газовой шахты"
            :action (fn [state]
                      (s5-3-2-action state))
            :edges []
            :parent :s5
            :type :normal}
   :s5.4.1 {:discription "Выбрать рабочего"
            :action (fn [state]
                      (s5-4-1-action state))
            :edges [{:state :s5.4.2
                     :discription "Выбран"
                     :valid? (fn [state]
                               (s5-4-1-valid-s5-4-2 state))}]
            :parent :s5
            :type :normal}
   :s5.4.2 {:discription "Отправить выбранного рабочего WorkerManager на постройку Депо"
            :action (fn [state]
                      (s5-4-2-action state))
            :edges []
            :parent :s5
            :type :normal}
   :s6 {:discription "Выбрать рабочего"
        :action (fn [state]
                  (s6-action state))
        :edges [{:state :s7
                 :discription "Выбран"
                 :valid? (fn [state]
                           (s6-valid-s7 state))}]
        :type :normal}
   :s7 {:discription "Создать разведчика"
        :action (fn [state]
                  (s7-action state))
        :edges [{:state :s4
                 :discription "Разведчик создан"
                 :valid? (fn [state]
                           (s7-valid-s4 state))}]
        :type :normal}
   :s8 {:discription "Выбрать рабочего"
        :action (fn [state]
                  (s8-action state))
        :edges [{:state :s9
                 :discription "Выбран"
                 :valid? (fn [state]
                           (s8-valid-s9 state))}]
        :type :normal}
   :s9 {:discription "Отправить выбранного рабочего WorkerManager на постройку Фабрики"
        :action (fn [state]
                  (s9-action state))
        :edges [{:state :s8
                 :discription "1 Фабрика построена"
                 :valid? (fn [state]
                           (s9-valid-s8 state))}
                {:state :s10
                 :discription "2 Фабрика построена"
                 :valid? (fn [state]
                           (s9-valid-s10 state))}]
        :type :normal}
   :s10 {:discription "Построить защиту"
         :action (fn [state]
                   (s10-action state))
         :edges [{:state :s11
                  :discription "Защита построена"
                  :valid? (fn [state]
                            (s10-valid-s11 state))}]
         :type :normal}
   :s11 {:discription "Построить защиту"
         :action (fn [state]
                   (s11-action state))
         :edges [{:state :s12
                  :discription "Получена команда"
                  :valid? (fn [state]
                            (s11-valid-s12 state))}
                 {:state :s13
                  :discription "База атакована"
                  :valid? (fn [state]
                            (s11-valid-s13 state))}]
         :type :normal}
   :s12 {:discription "Выполнить команду"
         :action (fn [state]
                   (s12-action state))
         :edges [{:state :s11
                  :discription "Команда выполнена"
                  :valid? (fn [state]
                            (s12-valid-s11 state))}]
         :type :normal}
   :s13 {:discription "Защитить базу"
         :action (fn [state]
                   (s13-action state))
         :edges [{:state :s11
                  :discription "База в безопасности"
                  :valid? (fn [state]
                            (s13-valid-s11 state))}]
         :type :normal}
   :edges []})

(def mtemplate
  "Model template for researcher"
  (m/make-mtemplate template start-state))

(def model
  "Researcher micromanager model"
  (m/make-model mtemplate (init-data)
                :on-unit-create (fn [state unit]
                                  (on-unit-create state unit))))


