(ns cardinal.bot
  (:require
   [taoensso.timbre :as timbre
    :refer [log  trace  debug  info  warn  error  fatal  report
            logf tracef debugf infof warnf errorf fatalf reportf
            spy get-env]]
   [cardinal.bot.managers :as m]
   [cardinal.interop.base :as b]
   [cardinal.bot.models :refer [models]]
   [cardinal.bot.utils :as u]))

;;;;;;;;;;;;;;;;;;;;
;; Base listeners ;;
;;;;;;;;;;;;;;;;;;;;

(defn on-end!
  [state is-winner]
  @state)

(defn on-frame!
  [state]
  (info "Frame")
  (info @state)
  (if (empty? (:micromanagers @state))
    (do
      (info "Create managers...")
      (u/create-base-micromanagers! state))
    (do
      (info "Update micromanagers...")
      (update-in @state [:micromanagers] m/update-managers))))


(defn on-nuke-detect!
  [state target]
  @state)

(defn on-player-dropped!
  [state player]
  @state)

(defn on-player-left!
  [state player]
  @state)

(defn on-receive-text!
  [state player text]
  @state)

(defn on-save-game!
  [state game-name]
  @state)

(defn on-send-text!
  [state text]
  @state)

(defn on-start!
  [state]
  (info "Start game")
  (let [mirror (:mirror @state)
        game (b/get-game mirror)
        self (b/get-self game)
        allies (b/get-allies game)
        enemies (b/get-enemies game)]
    (do
      (info "Read map...")
      (b/read-map)
      (info "Analayze map...")
      (b/analayze-map))
    (-> @state
        (assoc :enemies (apply vector enemies))
        (assoc :allies (apply vector allies))
        (assoc :player self)
        (assoc :game game))))

(defn on-unit-complete!
  [state unit]
  @state)

(defn on-unit-create!
  [state unit]
  @state)

(defn on-unit-destroy!
  [state unit]
  @state)

(defn on-unit-discover!
  [state unit]
  @state)


(defn on-unit-evade!
  [state unit]
  @state)

(defn on-unit-hide!
  [state unit]
  @state)

(defn on-unit-morph!
  [state unit]
  @state)

(defn on-unit-renegade!
  [state unit]
  @state)

(defn on-unit-show!
  [state unit]
  @state)
