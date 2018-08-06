(ns cardinal.bot.state
  (:require
   [clojure.spec.alpha :as s])
  (:import
   [bwapi Mirror])
  (:use
   [cardinal.interop.base]
   [cardinal.interop.type]))


;;;;;;;;;;;
;; State ;;
;;;;;;;;;;;
;;
;; Main bot structure fun
;;

;;
;; spec type
;;
;;(s/def ::mirror
;;  :bwapi.mirror/all)
;;
;;(s/def ::game
;;  :bwapi.game/all)
;;
;;(s/def ::self
;;  :bwapi.player/all)
;;
;;(s/def ::workers
;;  s/coll-of:bwapi.unit-type/worker)
;;
;;(s/def ::units
;;  (s/keys
;;   :req-un [::workers ::]))
;;
;;Fix
;;(s/def ::research-info
;;  :bwapi.player/all)
;;
;;;; main spec type
;;(s/def :cardinal.bot.state/type
;;  (s/keys
;;   :req-un [::mirror ::game
;;            ::self   ::research-info
;;            ::units]))

;;
;; Defoult state
;;
(def ^:const default-stat
  {:mirror (Mirror.)
   :game nil ; Game
   :self nil
   :research-info {}
   :units {:workers []
           :barraks []}})

;;
;; State fun
;;

(defn state-get-self
  [state]
  (get state :self))

(defn state-get-game
  [state]
  (get state :game))


(defn state-get-all-units
  [state]
  (get-units (state-get-self state)))

(defn state-get-research-info
  [state]
  (get state :research-info))

(defn state-get-units-barraks
  [state]
  (get-in state [:units :barraks]))

(defn state-get-units
  [state]
  (get state :units))

(defn state-get-minerals-count
  [state]
  (let [self (state-get-self state)]
    (player-get-minerals self)))

(defn state-get-minerals-field
  [state]
  (let [game (state-get-game state)
        neural-player (game-get-neutral game)
        neural-units (get-units neural-player)
        minerals (filter #(unit-is-mineral-field %) neural-units)]
    minerals))

(defn state-get-minerals-field-count
  [state]
  (count (state-get-minerals-field state)))

