(ns cardinal.bot
  (:require
   [taoensso.timbre :as timbre
    :refer [log  trace  debug  info  warn  error  fatal  report
            logf tracef debugf infof warnf errorf fatalf reportf
            spy get-env]]
   [cardinal.bot.managers :as m] [cardinal.interop.base :as b]
   [cardinal.bot.models :refer [models]]
   [cardinal.bot.utils :as u]))

;;;;;;;;;;;;;;;;;;;
;; Bot functions ;;
;;;;;;;;;;;;;;;;;;;

(defn create-macromanagers
  [state]
  [(m/make-macromanager
    state
    (:base-manager models))])

(defn update-managers
  "Update macro and micromanagers"
  ([state]
   (update-managers state nil))
  ([state event]
   (-> state
       (update-in [:micromanagers] m/update-managers event)
       (update-in [:macromanagers] m/update-managers event))))

(defmacro defn-event-handler
  "Create event handler. It update managers and log params.

  *name* - handler name.

  *event* - event.

  *params* - vector where in odd position - debug parameter name, in even position - parameter name
  "
  [name event params]
  (assert (even? (count params)))
  (let [params-var (take-nth 2 params)
        params-name (take-nth 2 (rest params))]
    `(defn ~name
       ~(into [] (concat ['state-ref] params-var))
       (info ~(str "Event: " (name event)))
       (debug "State: " @state-ref)
       ~@(map #(list 'debug
                     (str %1 ": ") %2)
              params-name
              params-var)
       (info "Update managers...")
       (update-managers @state-ref ~(keyword event)))))

;;;;;;;;;;;;;;;;;;;;
;; Base listeners ;;
;;;;;;;;;;;;;;;;;;;;

(defn on-start!
  [state-ref]
  (info "Start game")
  (let [mirror (:mirror @state-ref)
        game (b/get-game mirror)
        self (b/get-self game)
        allies (b/get-allies game)
        enemies (b/get-enemies game)
        macromanagers (create-macromanagers)
        new-state (-> @state-ref
                      (assoc :macromanagers macromanagers)
                      (assoc :enemies (apply vector enemies))
                      (assoc :allies (apply vector allies))
                      (assoc :player self)
                      (assoc :game game))]
    (do
      (info "Read map...")
      (b/read-map)
      (info "Analayze map...")
      (b/analayze-map))
    (debug "State: " new-state)
    new-state))

(defn-event-handler on-end!
  :end
  [is-winner "Is winer"])

(defn-event-handler on-frame!
  :frame
  [])

(defn-event-handler on-nuke-detect!
  :nuke-detect
  [target "Target"])

(defn-event-handler on-player-dropped!
  :player-dropped
  [player "Player"])

(defn-event-handler on-player-left!
  :player-left
  [player "Player"])

(defn-event-handler on-receive-text!
  :receive-text
  [player "Player"
   text "Text"])

(defn-event-handler on-save-game!
  :save-game
  [game-name "Game name"])

(defn-event-handler on-send-text!
  :send-text
  [text "Text"])

(defn-event-handler on-unit-complete!
  :unit-complete
  [unit "Unit"])

(defn-event-handler on-unit-create!
  :unit-create
  [unit "Unit"])

(defn-event-handler on-unit-destroy!
  :unit-destroy
  [unit "Unit"])

(defn-event-handler on-unit-discover!
  :unit-discover
  [unit "Unit"])

(defn-event-handler on-unit-evade!
  :unit-evade
  [unit "Unit"])

(defn-event-handler on-unit-hide!
  :unit-hide
  [unit "Unit"])

(defn-event-handler on-unit-morph!
  :unit-morph
  [unit "Unit"])

(defn-event-handler on-unit-renegade!
  :unit-renegade
  [unit "Unit"])

(defn-event-handler on-unit-show!
  :unit-show
  [unit "Unit"])
