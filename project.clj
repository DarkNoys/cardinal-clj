(defproject cardinal "0.1.0-SNAPSHOT"
  :description ""
  :url ""
  :main Cardinal
  :aot [cardinal.core]
  :resource-paths ["resource/" "lib/bwmirror_v2_6.jar" "lib/fsm-clj.jar"]
  :profiles {:uberjar {:aot :all
                       :main Cardinal}}
  :injections [(remove-method print-method clojure.lang.IDeref)
               (defmethod print-method clojure.lang.Atom [a ^java.io.Writer w]
                 (.write w (str "#<" a ">")))]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 [clj-time "0.15.0"]
                 [com.taoensso/timbre "4.10.0"]])
