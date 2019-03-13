(ns cardinal.bot.models.worker
  (:require
   [taoensso.timbre :as timbre
    :refer [log  trace  debug  info  warn  error  fatal  report
            logf tracef debugf infof warnf errorf fatalf reportf
            spy get-env]] [fsm-clj.fsm :as fsm]
   [cardinal.bot.managers :as m]
   [cardinal.interop.base :as b]))

;;;;;;;;;
;; Doc ;;
;;;;;;;;;
;;

;;;;;;;;;;;;;;;
;; Init data ;;
;;;;;;;;;;;;;;;

(defn init-data
  []
  {:in-action []
   :cur-command nil})

;;;;;;;;;;;;;;;;
;; Start state;;
;;;;;;;;;;;;;;;;

(def start-state
  :s1)

;;;;;;;;;;;;;;;;;;;
;; Command types ;;
;;;;;;;;;;;;;;;;;;;

(defn get-type
  [state]
  (:type state))

(defn mine-ore?
  [dtype]
  (= dtype :mine-ore))

(defn mine-gas?
  [dtype]
  (= dtype :mine-gas))

(defn building?
  [dtype]
  (= dtype :building))

;;;;;;;;;;;;;
;; Actions ;;
;;;;;;;;;;;;;

;; S1

(defn s1-action
  [{:keys [chan cur-command] :as state}]
  (info "Worker: wait command...")
  (info @chan)
  (if (nil? cur-command)
    (dosync
     (let [x (peek @chan)]
       (if (nil? x)
         state
         (do
           (send chan pop)
           (assoc state
                  [:cur-command]
                  x)))))
    state))

(defn s1-valid-s2-1
  [{:keys [chan cur-command] :as state}]
  (info "Worker: s1 -> s2 ...")
  (if (nil? cur-command)
    false
    (-> cur-command
        (get-type)
        (mine-ore?))))

(defn s1-valid-s2-2
  [{:keys [chan cur-command] :as state}]
  (if (nil? cur-command)
    false
    (-> cur-command
        (get-type)
        (mine-gas?))))

(defn s1-valid-s2-3
  [{:keys [chan cur-command] :as state}]
  (if (nil? cur-command)
    false
    (-> cur-command
        (get-type)
        (building?))))

;; S2

(defn s2-valid-s1
  [state]
  false)

;; S2.1

(defn s2-1-action
  [{:keys [main-state cur-command] :as state}]
  (let [game (:game @main-state)
        player (:player @main-state)
        minerals (b/game-get-minerals game)
        sl (b/player-get-start-location player)
        near-mineral (->> minerals
                             (map #(b/tile-position-get-distance
                                    (b/unit-get-tile-position %)
                                    sl))
                             (sort)
                             (first))]
    (if (nil? near-mineral)
      state
      (do
        (let [worker (get-in cur-command
                             [:data :unit])]
          (b/unit-gather worker near-mineral))
        (assoc state :cur-command nil)))))

;; S2.2

(defn s2-2-action
  [state]
  state)

;; S2.3

(defn s2-3-action
  [state]
  state)




;;;;;;;;;;;;;;
;; Template ;;
;;;;;;;;;;;;;;

(def template
  {:s1 {:discription "Wait a command"
        :action (fn [state]
                  (s1-action state))
        :edges [{:state :s2.1
                 :discription "Mine ore"
                 :valid? (fn [state]
                           (s1-valid-s2-1 state))}
                {:state :s2.2
                 :discription "Mine gas"
                 :valid? (fn [state]
                           (s1-valid-s2-2 state))}
                {:state :s2.3
                 :discription "build"
                 :valid? (fn [state]
                           (s1-valid-s2-3 state))}]
        :type :normal}
   :s2 {:discription "Command execution"
        :edges [{:state :s1
                 :discription "Command started to run"
                 :valid? (fn [state]
                           (s2-valid-s1 state))}]
        :type :heir
        :start-state :s2.1}
   :s2.1 {:discription "Send units to mine"
          :type :normal
          :parent :s2
          :action (fn [state]
                    (s2-1-action state))
          :edges []}
   :s2.2 {:discription "Send units to gas"
          :type :normal
          :parent :s2
          :action (fn [state]
                    (s2-2-action state))
          :edges []}
   :s2.3 {:discription "Build"
          :type :normal
          :parent :s2
          :action (fn [state]
                    (s2-3-action state))
          :edges []}})

(def mtemplate
  "Model template for researcher"
  (m/make-mtemplate template start-state))

(def model
  "Researcher micromanager model"
  (m/make-model mtemplate (init-data)))


