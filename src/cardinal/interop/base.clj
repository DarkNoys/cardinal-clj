(ns cardinal.interop.base
  (:import
   [bwapi UnitType]
   [bwta BWTA]))

;;;;;;;;;;
;; Unit ;;
;;;;;;;;;;

(defn unit-move
  [unit position]
  (.move unit position))

(defn unit-get-type
  [unit]
  (.getType unit))

(defn unit-is-idle?
  [unit]
  (.isIdle unit))

(defn unit-can-build
  ([unit utype]
   (.canBuild unit utype))
  ([unit utype pos]
   (.canBuild unit utype pos)))

(defn unit-can-train
  [unit utype]
  (.canTrain unit utype))

(defn unit-train
  [unit utype]
  (.train unit utype))

(defn unit-build
  [unit utype pos]
  (.build unit utype pos))

(defn unit-gather
  [unit res]
  (.gather unit res))

(defn unit-get-distance
  [unit target]
  (.getDistance unit target))

(defn unit-get-tile-position
  [unit]
  (.getTilePosition unit))

(defn unit-is-worker?
  [unit]
  (let [u-type (unit-get-type unit)]
    (.isWorker u-type)))

(defn unit-is-mineral-field
  [unit]
  (.isMineralField (unit-get-type unit)))

(defn unit-get-player
  [unit]
  (.getPlayer unit))

;;;;;;;;;;;;;;;
;; Unit-type ;;
;;;;;;;;;;;;;;;

(defn unit-type-is-refinery
  [utype]
  (.isRefinery utype))

(defn unit-type-is-mineral-field
  [utype]
  (.isMineralField utype))

;;;;;;;;;;;;;;;;;;;
;; Base Location ;;
;;;;;;;;;;;;;;;;;;;

(defn bl-get-position
  [bl]
  (.getPosition bl))

;;;;;;;;;;;;;;;;;;;
;; Base-Location ;;
;;;;;;;;;;;;;;;:;;;

(defn base-get-position
  [base]
  (.getPosition base))

(defn base-get-title-position
  [base]
  (.getTilePosition base))

(defn base-get-region
  [base]
  (.getRegion base))

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
  (.startGame mirror))

(defn get-game
  [mirror]
  (.getGame mirror))

(defn set-mirror-event-listener
  [mirror listener]
  (.setEventListener (.getModule mirror) listener))

;;;;;;;;;;;;;;
;; Position ;;
;;;;;;;;;;;;;;

(defn position-get-approx-distance
  [pos other-pos]
  (.getApproxDistance pos other-pos))

;;;;;;;;;;
;; Tile ;;
;;;;;;;;;;

(defn tile-position-get-distance
  [pos other-pos]
  (.getDistance pos other-pos))


;;;;;;;;;;
;; Game ;;
;;;;;;;;;;

(defn game-leave-game
  [game]
  (.leaveGame game))

(defn game-get-neutral
  [game]
  (.neutral game))

(defn game-get-minerals
  [game]
  (-> game
      (game-get-neutral)
      (#(filter (fn [unit]
                  (-> unit
                      (unit-get-type)
                      (unit-type-is-mineral-field)))
                %))))


(defn get-allies
  [game]
  (.allies game))

(defn get-enemies
  [game]
  (.enemies game))

(defn get-self
  [game]
  (.self game))

(defn game-can-build-here
  [game tpos unit-type]
  (.canBuildHere game tpos unit-type))

(defn game-get-all-units
  [game]
  (.getAllUnits game))

;;;;;;;;;;;;
;; Player ;;
;;;;;;;;;;;;

(defn get-units
  [player]
  (.getUnits player))

(defn player-get-minerals
  [player]
  (.minerals player))

(defn player-get-start-location
  [player]
  (.getStartLocation player))
