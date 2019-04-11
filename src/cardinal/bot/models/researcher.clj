(ns cardinal.bot.models.researcher
  (:require
   [taoensso.timbre :as timbre
    :refer [log  trace  debug  info  warn  error  fatal  report
            logf tracef debugf infof warnf errorf fatalf reportf
            spy get-env]]
   [fsm-clj.fsm :as fsm]
   [cardinal.bot.managers :as m]
   [cardinal.interop.base :as b]))

(defn init-data
  []
  {:base-list []
   :current-base-region nil
   :current-base nil})

(def start-state
  :s1)

;;;;;;;;;;;;;
;; Actions ;;
;;;;;;;;;;;;;

;; S1

(defn s1-action
  [{:keys [data] :as state}]
  (let [base-list (get data :base-list)]
    (info "Researcher: get base-list..")
    (if (empty? base-list)
      (update-in state [:data :base-list]
                 (fn [_]
                   (apply vector
                          (b/get-start-locations))))
      state)))

(defn s1-valid-s2
  [{:keys [data] :as state}]
  (let [res (seq (get data
                      :base-list))]
    (info (format "Researcher: s1 -> s2 = %b" res))
    res))


;; S2

(defn s2-in-action
  [{:keys [data] :as state}]
  (let [base-list (get data :base-list)]
    (let [base (peek base-list)
          other-bases (pop base-list)
          region (b/get-region base)]
      (info (count other-bases))
      (info (count base-list))
      (-> state
          (assoc-in [:data :base-list]
                    other-bases)
          (assoc-in [:data :current-base]
                    base)
          (assoc-in [:data :current-base-region]
                    region)))))

(defn s2-valid-s3
  [{:keys [data] :as state}]
  (let [res (not
             (nil?
              (get data
                   :current-base)))]
    (info (format "Researcher: s2 -> s3 = %b" res))
    res))

;; S3

(defn s3-action
  [{:keys [data] :as state}]
  state)

(defn s3-valid-s4
  [state]
  false)

;; S3-1

(defn s3-1-action
  [{:keys [sub-units data] :as state}]
  (let [base (get data :current-base)
        unit (first sub-units)
        base-position (b/get-position base)]
    (info (format "Researcher: Go to the base"))
    (info (b/get-tile-position unit))
    (if (b/is-idle? unit)
      (b/move unit base-position)))
  state)

(defn s3-1-valid-s3-2
  [{:keys [sub-units data] :as state}]
  (let [base (get data :current-base)
        unit (first sub-units)
        base-position (b/get-title-position base)
        res (> 10 (.getDistance base-position
                                (b/get-tile-position unit)))]
    (info (format "Researcher: s3-1 -> s3-2 = %b" res))
    res))


;; S3-2

(defn s3-2-action
  [state]
  state)

(defn s3-2-valid-s3-3
  [state]
  true)

;; S3-2-1

(defn s3-2-1-action
  [state]
  state)

(defn s3-2-1-valid-s3-2-2
  [state]
  false)

(defn s3-2-1-valid-s3-2-3
  [state]
  false)

;; S3-2-2

(defn s3-2-2-action
  [state]
  state)

(defn s3-2-2-valid-s3-2-1
  [state]
  false)

;; S3-2-3

(defn s3-2-3-action
  [state]
  state)

(defn s3-2-3-valid-s3-2-1
  [state]
  false)

;; S3-4

(defn s3-3-action
  [state]
  state)

(defn s3-3-valid-s2
  [{:keys [data] :as state}]
  (let [base-list (get data :base-list)
        res (seq base-list)]
    (info (format "Researcher: s3-3 -> s2 = %b" res))
    res))


(defn s3-3-valid-end
  [{:keys [data] :as state}]
  (let [base-list (get data :base-list)
        res (empty? base-list)]
    (info base-list)
    (info (format "Researcher: s3-3 -> end = %b" res))
    res))

;; S4

(defn s4-action
  [state]
  state)

(defn s4-valid-s3
  [state]
  false)

(defn s4-valid-s4
  [state]
  false)




(def template
  {:s1 {:discription "Wait for a list of bases"
        :action (fn [state] (s1-action state))
        :edges [{:state :s2
                 :discription "Received the list of bases"
                 :valid? (fn [state] (s1-valid-s2 state))}]
        :type :normal}
   :s2 {:discription "Select a base"
        :in-action (fn [state] (s2-in-action state))
        :edges [{:state :s3
                 :discription "A base selected"
                 :valid? (fn [state] (s2-valid-s3 state))}]
        :type :normal}
   :s3 {:discription "Exploration"
        :action (fn [state] (s3-action state))
        :edges [{:state :s4
                 :discription "A rival spotted"
                 :valid? (fn [state] (s3-valid-s4 state))}]
        :type :heir
        :start-state :s3-1}
   :s3-1 {:discription "Move to the base"
          :parent :s3
          :action (fn [state] (s3-1-action state))
          :edges [{:state :s3-2
                   :discription "The base reached"
                   :valid? (fn [state] (s3-1-valid-s3-2 state))}]
          :type :normal}
   :s3-2 {:discription "Look up for resoueces and buildings"
          :parent :s3
          :action (fn [state] (s3-2-action state))
          :edges [{:state :s3-3
                   :discription "Base explored, rival not registered"
                   :valid? (fn [state] (s3-2-valid-s3-3 state))}]
          :type :heir
          :start-state :s3-2-1}
   :s3-2-1 {:discription "Look up"
            :parent :s3-2
            :action (fn [state] (s3-2-1-action state))
            :edges [{:state :s3-2-2
                     :discription "Resources found"
                     :valid? (fn [state] (s3-2-1-valid-s3-2-2 state))}
                    {:state :s3-2-3
                     :discription "Buildings found"
                     :valid? (fn [state] (s3-2-1-valid-s3-2-3 state))}]
            :type :normal}
   :s3-2-2 {:discription "Storage of resources coords"
            :parent :s3-2
            :action (fn [state] (s3-2-2-action state))
            :edges [{:state :s3-2-1
                     :discription "Save resource"
                     :valid? (fn [state] (s3-2-2-valid-s3-2-1 state))}]
            :type :normal}
   :s3-2-3 {:discription "Storage of buildings coords"
            :parent :s3-2
            :action (fn [state] (s3-2-3-action state))
            :edges [{:state :s3-2-1
                     :discription "Save building"
                     :valid? (fn [state] (s3-2-3-valid-s3-2-1 state))}]
            :type :normal}
   :s3-3 {:discription "Check the list"
          :parent :s3
          :action (fn [state] (s3-3-action state))
          :edges [{:state :end
                   :discription "The list of bases is empty"
                   :valid? (fn [state] (s3-3-valid-end state))}
                  {:state :s2
                   :discription "The list of bases is not empty"
                   :valid? (fn [state] (s3-3-valid-s2 state))}]
          :type :normal}
   :s4 {:discription "Increase distance to the rival"
        :action (fn [state] (s4-action state))
        :edges [{:state :s4
                 :discription "Increase distance to the rival"
                 :valid? (fn [state] (s4-valid-s4 state))}
                {:state :s3
                 :discription "Safe distance to the rival"
                 :valid? (fn [state] (s4-valid-s3 state))}]
        :type :normal}
   :end {:type :normal
         :edges []}})

(def mtemplate
  "Model template for researcher"
  (m/make-mtemplate template start-state))

(def model
  "Researcher micromanager model"
  (m/make-model mtemplate (init-data)))


