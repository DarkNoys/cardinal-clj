(ns cardinal.bot.utils
  (:require
   [taoensso.timbre :as timbre
    :refer [log  trace  debug
            info  warn  error
            fatal  report logf
            tracef debugf infof
            warnf errorf fatalf
            reportf spy get-env]]
   [cardinal.bot.managers :as m]
   [cardinal.interop.base :as b]
   [cardinal.bot.models :refer [models]]))

(defn split-eq
  [n lst]
  (let [lst-count (count lst)
        fsize (max 1
                   (int
                    (Math/ceil
                     (quot
                      lst-count
                      n))))]
    (loop [acc []
           nlst lst]
      (let [[x xl] (split-at fsize nlst)]
        (if (empty? xl)
          (if (empty? x)
            acc
            (conj acc x))
          (recur (conj acc x) xl))))))

(defmacro pdoseq
  [thread-count [sym coll] & body]
  `(do (doall (pmap
               (fn [vals#]
                 (doseq [~sym vals#]
                   ~@body))
               (split-eq ~thread-count ~coll)))
       nil))


(defn create-base-micromanagers!
  "Create base micromanagers on start"
  [state]
  (let [player (:player @state)
        units (b/get-units player)
        workers (filter b/unit-is-worker? units)]
    (if (empty? (:micromanagers state))
      (let [worker (first workers)
            worker-manager (m/make-micromanager
                            state
                            []
                            (:worker models))
            researcher-manager (m/make-micromanager
                                state
                                [worker]
                                (:researcher models))]
        (info "Create reseach manager...")
        (-> @state
            (update :micromanagers #(conj % (m/send-message
                                             worker-manager
                                             {:type :mine-ore
                                              :data {:unit (second workers)}})))
            (update :micromanagers #(conj % researcher-manager))))
      @state)))

