(ns cardinal.core
  (:require
   [cardinal.bot :as b])
  (:use
   [cardinal.interop.base]
   [cardinal.interop.type])
  (:import
   (bwapi.*)
   (clojure.lang.IDeref)))


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
             :game nil ; Game
             :self nil
             :research-info {}
             :units {:worker []
                     :barraks []}})])

(defn cardinal-run
  [this]
  (let [mirror (get-in @(.state this) [:mirror])]
    (set-mirror-event-listener mirror this)
    (start-game mirror)))

(defn cardinal-main
  [& args]
  (let [ai (cardinal.core.)]
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
  (b/on-end! (.state this) is-winner))

(defn cardinal-onFrame
  [this]
  (b/on-frame! (.state this)))

(defn cardinal-onNukeDetect
  [this target]
  (b/on-nuke-detect! (.state this) target))

(defn cardinal-onPlayerDropped
  [this player]
  (b/on-player-dropped! (.state this) player))

(defn cardinal-onPlayerLeft
  [this player]
  (b/on-player-left! (.state this) player))

(defn cardinal-onReceiveText
  [this player text]
  (b/on-receive-text! (.state this) player text))

(defn cardinal-onSaveGame
  [this game-name]
  (b/on-save-game! (.state this) game-name))

(defn cardinal-onSendText
  [this text]
  (b/on-send-text! (.state this) text))

(defn cardinal-onStart
  [this]
  (b/on-start! (.state this)))

(defn cardinal-onUnitComplete
  [this unit]
  (b/on-unit-complete! (.state this) unit))

(defn cardinal-onUnitCreate
  [this unit]
  (b/on-unit-create! (.state this) unit))

(defn cardinal-onUnitDestroy
  [this unit]
  (b/on-unit-destroy! (.state this) unit))

(defn cardinal-onUnitDiscover
  [this unit]
  (b/on-unit-discover! (.state this) unit))

(defn cardinal-onUnitEvade
  [this unit]
  (b/on-unit-evade! (.state this) unit))

(defn cardinal-onUnitHide
  [this unit]
  (b/on-unit-hide! (.state this) unit))

(defn cardinal-onUnitMorph
  [this unit]
  (b/on-unit-morph! (.state this) unit))

(defn cardinal-onUnitRenegade
  [this unit]
  (b/on-unit-renegade! (.state this) unit))

(defn cardinal-onUnitShow
  [this unit]
  (b/on-unit-show! (.state this) unit))
