(ns cardinal.bot.models
  (:require
   [cardinal.bot.models.base-manager :as base-manager]
   [cardinal.bot.models.worker :as worker]
   [cardinal.bot.models.researcher :as researcher]))

(def models
  {:researcher researcher/model
   :base-manager base-manager/model
   :worker worker/model})
