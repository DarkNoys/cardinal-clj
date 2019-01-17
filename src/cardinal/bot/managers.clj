(ns cardinal.bot.managers
  (:require
   [fsm-clj.fsm :as fsm]
   [cardinal.bot.utils :as u]
   [cardinal.interop.base :as b]
   [clj-time.local :as l]))

(defn make-mtemplate
  [template start-state]
  (partial fsm/make-fsm template start-state))

(defn make-model
  ([template start-state init-data]
   {:init-data init-data
    :mtemplate (make-mtemplate template start-state)})
  ([mtemplate init-data]
   {:init-data init-data
    :mtemplate mtemplate}))

(defn make-micromanager
  [main-state sub-unit model & {:keys [discription]}]
  (let [init-data (:init-data model)
        mtemplate (:mtemplate model)
        data {:main-state main-state
              :sub-unit sub-unit
              :data init-data}]
    {:discription discription
     :fsm (mtemplate data)}))

(defn make-macromanager
  [main-state sub-unit sub-managers model & {:keys [discription]}]
  (let [init-data (:init-data model)
        mtemplate (:mtemplate model)
        data {:main-state main-state
              :sub-managers sub-managers
              :sub-unit sub-unit
              :data init-data}]
    {:discription discription
     :fsm (mtemplate data)}))

(defn update-manager
  [manager]
  (fsm/update-fsm (:fsm manager)))

(defn update-managers
  [managers & {:keys [async] :or {async false}}]
  (u/pdoseq (if async
              1
              (+ 2 (.availableProcessors (Runtime/getRuntime))))
            [manager managers]
            (update-manager manager)))

;;;;;;;;;;;;
;; Models ;;
;;;;;;;;;;;;

;; Micromanagers
(def researcher-mtemplate
  "Model template for researcher"
  (let [start-state :s1
        template {:s1 {:discription "Wait for a list of bases"
                       :action (fn [state]
                                 (if (empty? (get-in state [:data :base-list]))
                                   (update-in state [:data :base-list]
                                              #(apply vector
                                                      (b/get-start-locations)))
                                   state))
                       :edges [{:state :s2
                                :discription "Received the list of bases"
                                :valid? (fn [state]
                                          (not (empty? (get-in state [:data :base-list]))))}]
                       :type :normal}
                  :s2 {:discription "Select a base"
                       :action (fn [state]
                                 (if (empty? (get-in state
                                                     [:data :current-base]))
                                   (update-in state [:data :current-base]
                                              (fn [_]
                                                (first
                                                 (get-in state
                                                         [:data :base-list]))))
                                   state))
                       :edges [{:state :s3
                                :discription "A base selected"
                                :valid? (fn [state]
                                          (not (nil? (get-in state [:data :current-base]))))}]
                       :type :normal}
                  :s3 {:discription "Exploration"
                       :edges [{:state :s4
                                :discription "A rival spotted"
                                :valid? nil?}]
                       :type :heir
                       :start-state :s3.1}
                  :s3.1 {:discription "Move to the base"
                         :parent :s3
                         :edges [{:state :s3.2
                                  :discription "The base reached"
                                  :valid? nil?}]
                         :type :normal}
                  :s3.2 {:discription "Look up for resoueces and buildings"
                         :parent :s3
                         :edges [{:state :s3.3
                                  :discription "Base explored, rival not registered"
                                  :valid? nil?}]
                         :type :heir
                         :start-state :s3.2.1}
                  :s3.2.1 {:discription "Look up"
                           :parent :s3.2
                           :edges [{:state :s3.2.2
                                    :discription "Resources found"
                                    :valid? nil?}
                                   {:state :s3.2.3
                                    :discription "Buildings found"
                                    :valid? nil?}]
                           :type :normal}
                  :s3.2.2 {:discription "Storage of resoueces coords"
                           :parent :s3.2
                           :edges []
                           :type :normal}
                  :s3.2.3 {:discription "Storage of buildings coords"
                           :parent :s3.2 :edges []
                           :type :normal}
                  :s3.3 {:discription "Remove the base"
                         :parent :s3
                         :edges [{:state :s3.4
                                  :discription "The base removed"
                                  :valid? nil?}]
                         :type :normal}
                  :s3.4 {:discription "Check the list"
                         :parent :s3
                         :edges [{:state :end
                                  :discription "The list of bases is empty"
                                  :valid? nil?}
                                 {:state :s3
                                  :discription "The list of bases is not empty"
                                  :valid? nil?}]
                         :type :normal}
                  :s4 {:discription "Increase distance to the rival"
                       :edges [{:state :s4
                                :discription "Increase distance to the rival"
                                :valid? nil?}
                               {:state :s3
                                :discription "Safe distance to the rival"
                                :valid? nil?}]
                       :type :normal}
                  :end {:type :normal
                        :edges []}}]
    (make-mtemplate template start-state)))

(def researcher-init-data
  {:base-list []
   :current-base nil})
(def researcher-model
  "Researcher micromanager model"
  (make-model researcher-mtemplate researcher-init-data))
