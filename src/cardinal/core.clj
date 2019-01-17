(ns cardinal.core
  (:require
   [cardinal.bot :as b]
   [cardinal.interop.base :refer :all]
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
 :name "cardinal.core"
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
             :alies []
             :player nil
             :macromanagers []
             :micromanagers []})])

(defn cardinal-run
  [this]
  (let [mirror (get-in @(.state this) [:mirror])]
    (set-mirror-event-listener mirror this)
    (start-game mirror)))

(defonce ^:dynamic *AI* (atom nil))
(defn cardinal-main
  [& args]
;;  (start-server :port 7888)
  (let [ai (cardinal.core.)]
    (timbre/refer-timbre)
    (timbre/merge-config!
     {:appenders
      {:spit
       (appenders/spit-appender
        {:fname "debug.log"})}})
        ;;{:fname (format
        ;;         "log/%s.log"
        ;;         (f/unparse
        ;;          (f/formatter "yyyy_MM_dd_H_m")
        ;;          (l/local-now))}})
    (reset! *AI* ai)
    (.run ai)))

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
  (reset! (.state this) (b/on-end! @(.state this) is-winner)))

(defn cardinal-onFrame
  [this]
  (reset! (.state this) (b/on-frame! @(.state this))))

(defn cardinal-onNukeDetect
  [this target]
  (reset! (.state this) (b/on-nuke-detect! @(.state this) target)))

(defn cardinal-onPlayerDropped
  [this player]
  (reset! (.state this) (b/on-player-dropped! @(.state this) player)))

(defn cardinal-onPlayerLeft
  [this player]
  (reset! (.state this) (b/on-player-left! @(.state this) player)))

(defn cardinal-onReceiveText
  [this player text]
  (reset! (.state this) (b/on-receive-text! @(.state this) player text)))

(defn cardinal-onSaveGame
  [this game-name]
  (reset! (.state this) (b/on-save-game! @(.state this) game-name)))

(defn cardinal-onSendText
  [this text]
  (reset! (.state this) (b/on-send-text! @(.state this) text)))

(defn cardinal-onStart
  [this]
  (reset! (.state this) (b/on-start! @(.state this))))

(defn cardinal-onUnitComplete
  [this unit]
  (reset! (.state this) (b/on-unit-complete! @(.state this) unit)))

(defn cardinal-onUnitCreate
  [this unit]
  (reset! (.state this) (b/on-unit-create! @(.state this) unit)))

(defn cardinal-onUnitDestroy
  [this unit]
  (reset! (.state this) (b/on-unit-destroy! @(.state this) unit)))

(defn cardinal-onUnitDiscover
  [this unit]
  (reset! (.state this) (b/on-unit-discover! @(.state this) unit)))

(defn cardinal-onUnitEvade
  [this unit]
  (reset! (.state this) (b/on-unit-evade! @(.state this) unit)))

(defn cardinal-onUnitHide
  [this unit]
  (reset! (.state this) (b/on-unit-hide! @(.state this) unit)))

(defn cardinal-onUnitMorph
  [this unit]
  (reset! (.state this) (b/on-unit-morph! @(.state this) unit)))

(defn cardinal-onUnitRenegade
  [this unit]
  (reset! (.state this) (b/on-unit-renegade! @(.state this) unit)))

(defn cardinal-onUnitShow
  [this unit]
  (reset! (.state this) (b/on-unit-show! @(.state this) unit)))
