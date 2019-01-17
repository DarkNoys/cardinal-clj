(ns cardinal.bot.utils
  (:require
   [cardinal.interop.base :as b]))

(defn split-eq
  [n lst]
  (let [lst-count (count lst)
        fsize (max 1 (int (Math/ceil (quot lst-count n))))]
    (loop [acc []
           nlst lst]
      (let [[x xl] (split-at fsize nlst)]
        (if (empty? xl)
          (conj acc x)
          (recur (conj acc x) xl))))))

(defmacro pdoseq
  [thread-count [sym coll] & body]
  `(do (doall (pmap
               (fn [vals#]
                 (doseq [~sym vals#]
                   ~@body))
               (split-eq ~thread-count ~coll)))
       nil))


