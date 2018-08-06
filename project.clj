(defproject cardinal "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:skip-aot cardinal.core
  :aot [cardinal.core]
  :resource-paths ["resource/" "lib/bwmirror_v2_6.jar"]
  :profiles {:uberjar {:aot :all}}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.taoensso/timbre "4.10.0"]])
