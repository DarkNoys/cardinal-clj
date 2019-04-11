(ns cardinal.core
  (:require
   [clojure.tools.nrepl.server :refer [start-server stop-server]]
   [cardinal.bot :as bot]
   [cardinal.interop.base :as b]
   [clj-time.local :as l]
   [clj-time.format :as f]
   [taoensso.timbre.appenders.core :as appenders]
   [taoensso.timbre :as timbre
    :refer [log  trace  debug  info  warn  error  fatal  report
            logf tracef debugf infof warnf errorf fatalf reportf
            spy get-env]])
  (:import
   [bwapi DefaultBWListener Mirror]
   [clojure.lang IDeref]))

;;
;; Main class fun
;;
(gen-class
 :name "Cardinal"
 :implements [clojure.lang.IDeref]
 :state state
 :init init
 :constructors {[] []}
 :extends bwapi.DefaultBWListener
 :main true
 :prefix "cardinal-"
 :methods [[run [] void]])


(defn cardinal-init
  []
  [[] (atom {:mirror (bwapi.Mirror.)
             :game nil
             :map-history []
             :enemies []
             :allies []
             :player nil
             :macromanagers []
             :micromanagers []})])

(defn cardinal-run
  [this]
  (let [mirror (get-in @(.state this) [:mirror])]
    (b/set-mirror-event-listener mirror this)
    (b/start-game mirror)))

(defn cardinal-main
  [& args]
  (let [ai (cardinal.core.)
        server (start-server :port 7632)]
    (try
      (timbre/refer-timbre)
      (timbre/merge-config!
       {:appenders
        {:spit
         (appenders/spit-appender
          ;;{:fname "log/debug.log"}
          {:fname (format
                   "log/debug_%s.log"
                   (f/unparse
                    (f/formatter "yyyy_MM_dd")
                    (l/ocal-now)))})}})
      (info "Start Bot")
      (.run ai)
      (finally
        (stop-server server)))))

;;
;; dref
;;

(defn cardinal-deref
  [this]
  @(.state this))

;;
;; Listener function
;;

(defn cardinal-onEnd
  [this is-winner]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-end! state is-winner))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onFrame
  [this]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-frame! state))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onNukeDetect
  [this target]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-nuke-detect! state target))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onPlayerDropped
  [this player]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-player-dropped! state player))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onPlayerLeft
  [this player]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-player-left! state player))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onReceiveText
  [this player text]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-receive-text! state player text))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onSaveGame
  [this game-name]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-save-game! state game-name))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onSendText
  [this text]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-send-text! state text))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onStart
  [this]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-start! state))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onUnitComplete
  [this unit]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-unit-complete! state unit))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onUnitCreate
  [this unit]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-unit-create! state unit))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onUnitDestroy
  [this unit]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-unit-destroy! state unit))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onUnitDiscover
  [this unit]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-unit-discover! state unit))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onUnitEvade
  [this unit]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-unit-evade! state unit))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onUnitHide
  [this unit]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-unit-hide! state unit))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onUnitMorph
  [this unit]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-unit-morph! state unit))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onUnitRenegade
  [this unit]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-unit-renegade! state unit))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))

(defn cardinal-onUnitShow
  [this unit]
  (let [state (.state this)
        old-state @state]
    (try
      (reset! state (bot/on-unit-show! state unit))
      (catch Exception e
        (do
          (error (.getMessage e))
          (reset! state old-state))))))
