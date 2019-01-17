(ns cardinal.interop.base
  (:import
   [bwapi UnitType]
   [bwta BWTA]))


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
  []
  (. bwta.BWTA readMap))

(defn analayze-map
  []
  (. bwta.BWTA analyze))

(defn get-start-locations
  []
  (. bwta.BWTA getStartLocations))

;;;;;;;;;;;;
;; Mirror ;;
;;;;;;;;;;;;

(defn start-game
  [mirror]
  (. mirror startGame))

(defn get-game
  [mirror]
  (. mirror getGame))

(defn set-mirror-event-listener
  [mirror listener]
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

(defn game-leave-game
  [game]
  (. game leaveGame))

(defn game-get-neutral
  [game]
  (. game neutral))

(defn get-enemies
  [game]
  (. game enemies))

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
   (. unit canBuild type))
  ([unit type pos]
   (. unit canBuild type pos)))

(defn unit-can-train
  [unit type]
  (. unit canTrain type))

(defn unit-train
  [unit type]
  (. unit train type))

(defn unit-build
  [unit type pos]
  (. unit build type pos))

(defn unit-gather
  [unit res]
  (. unit gather res))

(defn unit-get-distance
  [unit target]
  (. unit getDistance target))

(defn unit-get-tile-position
  [unit]
  (. unit getTilePosition))

(defn unit-is-worker?
  [unit]
  (let [u-type (unit-get-type unit)]
    (. u-type isWorker)))

(defn unit-is-mineral-field
  [unit]
  (. (unit-get-type unit) isMineralField))

(defn unit-get-player
  [unit]
  (. unit getPlayer))

;;;;;;;;;;;;;;;
;; Unit-type ;;
;;;;;;;;;;;;;;;

(defn unit-type-is-refinery
  [type]
  (. type isRefinery))


