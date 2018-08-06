(ns cardinal.interop.base
  (:require
   [clojure.spec.alpha :as s])
  (:import
   [bwapi.UnitType]
   [bwta.BWTA])
  (:use
   [cardinal.interop.type]))


;;;;;;;;;;;
;; Utils ;;
;;;;;;;;;;;

(defn swap-key
  [object key val]
  (merge object {key val}))

;;;;;;;;;;
;; BWTA ;;
;;;;;;;;;;

(defn read-map
  [] (. bwta.BWTA readMap))
(defn analayze-map [] (. bwta.BWTA analyze))


;;;;;;;;;;;;
;; Mirror ;;
;;;;;;;;;;;;

(defn start-game
  [mirror]
  {:pre [(s/valid? :bwapi.mirror/all mirror)]}
  (. mirror startGame))

(defn get-game
  [mirror]
  {:pre [(s/valid? :bwapi.mirror/all mirror)]
   :post [(s/valid? :bwapi.game/all %)]} (. mirror getGame))

(defn set-mirror-event-listener
  [mirror listener]
  {:pre [(s/valid? :bwapi.mirror/all mirror)
         (s/valid? :bwapi.bw-event-listener/all listener)]}
  (.setEventListener (.getModule mirror) listener))

;;;;;;;;;;;;;;
;; Position ;;
;;;;;;;;;;;;;;

(defn position-get-approx-distance
  [pos other-pos]
  (. pos getApproxDistance other-pos))

;;;;;;;;;;
;; Tile ;;
;;;;;;;;;;

(defn tile-position-get-distance
  [pos other-pos]
  (. pos getDistance other-pos))


;;;;;;;;;;
;; Game ;;
;;;;;;;;;;

(defn game-get-neutral
  [game]
  (. game neutral))

(defn get-self
  [game]
  (. game self))

(defn game-can-build-here
  [game tpos unit-type]
  (. game canBuildHere tpos unit-type))

(defn game-get-all-units
  [game]
  (. game getAllUnits))

;;;;;;;;;;;;
;; Player ;;
;;;;;;;;;;;;

(defn get-units
  [player]
  (. player getUnits))

(defn player-get-minerals
  [player]
  (. player minerals))


;;;;;;;;;;
;; Unit ;;
;;;;;;;;;;


(defn unit-get-type
  [unit]
  (. unit getType))

(defn unit-is-idle?
  [unit]
  (. unit isIdle))

(defn unit-can-build
  ([unit type]
   {:pre [(s/valid? :bwapi.unit/all
                    unit)
          (s/valid? :bwapi.unit-type/all
                    type)]}
   (. unit canBuild type))
  ([unit type pos]
   {:pre [(s/valid? :bwapi.unit/all
                    unit)
          (s/valid? :bwapi.unit-type/all
                    type)]}
   (. unit canBuild type pos)))

(defn unit-can-train
  [unit type]
  {:pre [(s/valid? :bwapi.unit/all
                   unit)
         (s/valid? :bwapi.unit-type/all
                   type)]}
  (. unit canTrain type))

(defn unit-train
  [unit type]
  {:pre [(s/valid? :bwapi.unit/all
                   unit)
         (s/valid? :bwapi.unit-type/all
                   type)
         (unit-can-train unit type)]}
  (. unit train type))

(defn unit-build
  [unit type pos]
  {:pre [(s/valid? :bwapi.unit/all
                   unit)
         (s/valid? :bwapi.unit-type/all
                   type)
         (unit-can-build unit type)]}
  (. unit build type pos))

(defn unit-gather
  [unit type]
  {:pre [(s/valid? :bwapi.unit/all
                   unit)]}
  (. unit gather type))

(defn unit-get-distance
  [unit target]
  (. unit getDistance target))

(defn unit-get-tile-position
  [unit]
  (. unit getTilePosition))

(defn unit-is-worker?
  [unit]
  {:pre [(s/valid? :bwapi.unit/all
                   unit)]}
  (let [u-type (unit-get-type unit)]
    (. u-type isWorker)))

(defn unit-is-mineral-field
  [unit]
  (. (unit-get-type unit) isMineralField))

;;;;;;;;;;;;;;;
;; Unit-type ;;
;;;;;;;;;;;;;;;

(defn unit-type-is-refinery
  [type]
  (. type isRefinery))



;;;;;;;;;;
;; BWTA ;;
;;;;;;;;;;
