(ns cardinal.bot.strategy
  (:require
   [cardinal.bot.alg :as alg :refer [get-build-tiles]]))

;;;;;;;;;;;;;;;;;;;;;
;; Worker strategy ;;
;;;;;;;;;;;;;;;;;;;;;

(defn gather-mineral!
  [state worker]
  (let [mineral-field (state-get-minerals-field state)
        sorted-mf (sort (fn [a b]
                           (-)
                           (unit-get-distance a worker)
                           (unit-get-distance b worker))
                        mineral-field)
        near-field (first sorted-mf)]
    (unit-gather worker near-field)
    state))


(defn worker-build-barrak!
  [state worker]
  (let [building-type bwapi.UnitType.Terran_Barrak
        build-tpos (get-build-tiles state building-type
                                    (unit-get-tile-position worker))
        build? (unit-build worker building-type build-tpos)]
    (if build?
      (println "Start build barraks")
      (println "Can't build barraks"))
    state))

(defn can-build-barrak?
  "return true else can and need build barraks"
  [state]
  (let [units (state-get-my-units state)
        minerals-count (state-get-minerals-count state)
        barraks (:barracks units)
        count-barracks (count barraks)]
    (and (>= minerals-count 150)
         (<= count-barracks 0))))

(defn worker-strategy!
  [state worker]
  (cond
    (can-build-barrak? state) (worker-build-barrak! state worker)
    :else (gather-mineral! state worker)))

(defn workers-strategy!
  [state]
  (let [my-units (state-get-my-units state)
        workers (:workers my-units)]
    (loop [new-state state
           [worker & rest-workers] workers]
      (if (empty? worker)
        new-state
        (recur (worker-strategy! new-state worker)
               rest-workers)))))

