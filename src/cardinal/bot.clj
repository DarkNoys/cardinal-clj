(ns cardinal.bot
  (:require
   [clojure.spec.alpha :as s])
  (:import
   [bwapi TilePosition])
  (:use
   [cardinal.bot state alg strategy]
   [cardinal.interop base type]))

;;;;;;;;;;;;;;;;;;;;
;; Base listeners ;;
;;;;;;;;;;;;;;;;;;;;

(defn on-end!
  [state is-winner]
  state)

(defn on-frame!
  [state]
  (do
    (research-proc! state)
    (worker-proc! state)))

(defn on-nuke-detect!
  [state target]
  state)

(defn on-player-dropped!
  [state player]
  state)

(defn on-player-left!
  [state player]
  state)

(defn on-receive-text!
  [state player text]
  state)

(defn on-save-game!
  [state game-name]
  state)

(defn on-send-text!
  [state text]
  state)

(defn on-start!
  [state]
  (let [mirror (:mirror @state)
        game (get-game mirror)
        self (get-self game)]
    (do
      (read-map)
      (analayze-map))
    (-> @state
        (swap-key :game self)
        (swap-key :self game))))

(defn on-unit-complete!
  [state unit]
  state)

(defn on-unit-create!
  [state unit]
  state)

(defn on-unit-destroy!
  [state unit]
  state)

(defn on-unit-discover
  [state unit]
  (let [unit-type (unit-get-type unit)]
    (condp #(s/valid? %1 %2) unit-type
      :bwapi.unit-type/terran-barracks (update-in state [:units :barracks]
                                                  conj unit)

      :bwapi.unit-type/worker (update-in state [:units :workers]
                                         conj unit)
      state)))


(defn on-unit-evade!
  [state unit]
  state)

(defn on-unit-hide!
  [state unit]
  state)

(defn on-unit-morph!
  [state unit]
  state)

(defn on-unit-renegade!
  [state unit]
  state)

(defn on-unit-show!
  [state unit]
  state)


