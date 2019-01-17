(defproject cardinal "0.1.0-SNAPSHOT"
  :description ""
  :url ""
  :main cardinal.core
  :aot [cardinal.core]
  :resource-paths ["resource/" "lib/bwmirror_v2_6.jar" "lib/fsm-clj.jar"]
  :profiles {:uberjar {:aot :all}}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-time "0.15.0"]
                 [com.taoensso/timbre "4.10.0"]])
