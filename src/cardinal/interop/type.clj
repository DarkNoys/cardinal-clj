(ns cardinal.interop.type
  (:require
   [clojure.spec.alpha :as s])
  (:import
   (bwapi.*))
  (:use
   [cardinal.interop.type-template]))


;; Macro

(defmacro gen-type-class-type-to-spec
  "Generate spec type from typeclass"
  [main-type java-class type-template-name type-template-method]
  `(s/def ~(keyword (str main-type "/"
                         type-template-name))
     #(= % (. ~java-class ~type-template-method))))

(defmacro gen-type-class-all-type-to-spec
  "Genrate spec types from typeclass in list"
  [main-type java-class type-template-list]
  `(doseq [[type-name#, type-method#] ~type-template-list]
     (eval `(gen-type-class-type-to-spec ~'~main-type
                                         ~'~java-class
                                         ~type-name#
                                         ~type-method#))))

(defmacro gen-java-class-to-spec
  "Generate spec type from java class"
  [main-type spec-name java-class-name]
  `(s/def ~(keyword (str main-type "." spec-name "/all"))
     #(instance? ~java-class-name %)))

(defmacro gen-all-java-class-to-spec
  "Genrate spec types from java class in list"
  [main-type java-class-template-list]
  `(doseq [[spec-name#, class-name#] ~java-class-template-list]
     (eval `(gen-java-class-to-spec ~'~main-type
                                    ~spec-name#
                                    ~class-name#))))


;; bwapi Java class spec define
(gen-all-java-class-to-spec bwapi bwapi-classes)

;; Unit types
(gen-type-class-all-type-to-spec bwapi.unit-type
                                 bwapi.UnitType unit-types)

;; Weapon types
(gen-type-class-all-type-to-spec bwapi.weapon-type
                                 bwapi.WeaponType weapon-types)

;; Upgrade types
(gen-type-class-all-type-to-spec bwapi.upgrade-type
                                 bwapi.UpgradeType upgrade-types)

;; Unit size types
(gen-type-class-all-type-to-spec bwapi.unit-size-type
                                 bwapi.UnitSizeType unit-size-types)

;; Unit command types
(gen-type-class-all-type-to-spec bwapi.unit-command-type
                                 bwapi.UnitCommandType unit-command-types)

;; Tech types
(gen-type-class-all-type-to-spec bwapi.tech-type
                                 bwapi.TechType tech-types)

;; Player types
(gen-type-class-all-type-to-spec bwapi.player-type
                                 bwapi.PlayerType player-types)

;; Game types
(gen-type-class-all-type-to-spec bwapi.game-type
                                 bwapi.GameType game-types)

;; ExplosionTypes
(gen-type-class-all-type-to-spec bwapi.explosion-type
                                 bwapi.ExplosionType explosion-types)

;; DamageTypes
(gen-type-class-all-type-to-spec bwapi.damage-type
                                 bwapi.DamageType damage-types)

;; BulletTypes
(gen-type-class-all-type-to-spec bwapi.bullet-type
                                 bwapi.BulletType bullet-types)

;;;;;;;;;;;;;;;;;
;; Other types ;;
;;;;;;;;;;;;;;;;;

;; Boolean
(s/def :bwapi.unit-type/worker
  #(. % isWorker))
