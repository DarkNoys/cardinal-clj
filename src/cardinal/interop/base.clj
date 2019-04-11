(ns cardinal.interop.base
  (:import
   [bwapi UnitType]
   [bwta BWTA]))

;;;;;;;;;;;;;;;;
;; Event type ;;
;;;;;;;;;;;;;;;;

(def ^{:const true}
  event-type
  #{:start
    :end
    :frame
    :nuke-detect
    :player-dropped
    :player-left
    :receive-text
    :save-game
    :send-text
    :unit-complete
    :unit-create
    :unit-destroy
    :unit-discover
    :unit-evade
    :unit-hide
    :unit-morph
    :unit-renegade
    :unit-show})

;;;;;;;;;;
;; Unit ;;
;;;;;;;;;;

(defn move
  [unit position]
  (.move unit position))

(defn get-type
  [unit]
  (.getType unit))

(defn is-idle?
  [unit]
  (.isIdle unit))

(defn can-build?
  ([unit utype]
   (.canBuild unit utype))
  ([unit utype pos]
   (.canBuild unit utype pos)))

(defn can-train?
  [unit utype]
  (.canTrain unit utype))

(defn train
  [unit utype]
  (.train unit utype))

(defn is-train?
  [unit]
  (.isTraining unit))

(defn build
  [unit utype pos]
  (.build unit utype pos))

(defn gather
  [unit res]
  (.gather unit res))

(defn get-distance
  [unit target]
  (.getDistance unit target))

(defn get-tile-position
  [unit]
  (.getTilePosition unit))

(defn is-worker?
  [unit-type]
  (.isWorker unit-type))

(defn is-mineral-field?
  [unit-type]
  (.isMineralField unit-type))

(defn get-player
  [unit]
  (.getPlayer unit))

;;;;;;;;;;;;;;;
;; Unit-type ;;
;;;;;;;;;;;;;;;

(defn type-is-refinery
  [utype]
  (.isRefinery utype))

(defn type-is-mineral-field
  [utype]
  (.isMineralField utype))

;;;;;;;;;;;;;;;;;;;
;; Base-Location ;;
;;;;;;;;;;;;;;;:;;;

(defn get-position
  [base]
  (.getPosition base))

(defn get-title-position
  [base]
  (.getTilePosition base))

(defn get-region
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

(defn get-approx-distance
  [pos other-pos]
  (.getApproxDistance pos other-pos))

;;;;;;;;;;
;; Tile ;;
;;;;;;;;;;

(defn get-distance
  [pos other-pos]
  (.getDistance pos other-pos))


;;;;;;;;;;
;; Game ;;
;;;;;;;;;;

(defn leave-game
  [game]
  (.leaveGame game))

(defn get-neutral
  [game]
  (.neutral game))

(defn game-get-minerals
  [game]
  (-> game
      (get-neutral)
      (#(filter (fn [unit]
                  (-> unit
                      (get-type)
                      (is-mineral-field?)))
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

(defn can-build-here
  [game tpos unit-type]
  (.canBuildHere game tpos unit-type))

(defn get-all-units
  [game]
  (.getAllUnits game))

;;;;;;;;;;;;
;; Player ;;
;;;;;;;;;;;;

(defn get-units
  [item]
  (.getUnits item))

(defn minerals
  [item]
  (.minerals item))

(defn get-start-location
  [item]
  (.getStartLocation item))
