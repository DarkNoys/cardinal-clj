(ns cardinal.bot.models
  (:require
   [fsm-clj.fsm :as fsm])
  (:require
   [cardinal.bot.models.worker :as worker]
   [cardinal.bot.models.researcher :as researcher]))

(def models
  {:researcher researcher/model
   :worker worker/model})
