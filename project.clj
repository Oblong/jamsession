(defproject
  jamsession "1.0.10"
  :description "jamsession: a non-persistent (in-memory-only) pool-tcp-server"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.oblong/jelly "0.1"]
                 [server-socket "1.0.0"]
                 [junit/junit "4.8.2"]
                 [javax.jmdns/jmdns "3.4.1"] ; previously: 3.2.2
                 [org.clojure/tools.cli "0.2.1"]]
  :plugins [[lein-marginalia "0.7.1"]]
  :profiles {:dev {:dependencies [[net.jcip/jcip-annotations "1.0"]]}}

  :repositories {"local" ~(str (.toURI (java.io.File. "maven_repository")))}

  :min-lein-version  "2.0.0"
  :main jamsession.jam
)
