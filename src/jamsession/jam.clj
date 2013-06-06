(ns jamsession.jam
  (:use [clojure.tools.cli :as tools])
  (:import [com.oblong.jelly.pool.mem TCPMemProxy])
  ; This is how we expose the startjamsession function to external Java callers
  (:gen-class
    :methods [#^{:static true} [startjamsession []           boolean]
              #^{:static true} [startjamsession [int]        boolean]
              #^{:static true} [startjamsession [int String] boolean]
              #^{:static true} [stopjamsession  []           void]]))

(def jam-service "Here is mutable application state" 
  (ref {:tcp-proxy nil :mdns-server nil}))

(defn serving-jam? [] (get @jam-service :tcp-proxy))

(defn make-mdns-announcer
  "Starts bonjour/zeroconf announcing, after an n-second pause"
  [port service-name n]
  (Thread/sleep (* n 1000))
  (let [m-server (javax.jmdns.JmDNS/create "localhost")
        options  (java.util.HashMap. {"description"
                                     (.getBytes "java-based pool-tcp-server")})
        service  (javax.jmdns.ServiceInfo/create
                      "_pool-server._tcp.local."
                      service-name
                      "_remote"
                      port
                      ;"jamsession pooltcpserver advertisement"
                      0 0 true options)]
    (.registerService m-server service)
    (println "::mDNS advertising a pool tcp server with service name:" service-name)
    (println "::mDNS service info:" service)
    m-server))

(defn shutdown-services
  "Stops the mDNS and tcp proxy services.
  Re-nils the jam-service ref contents afterwards."
  []
  (dosync (try (when-let [mdns (get @jam-service :mdns-server)]
                 (.unregisterAllServices mdns)
                 (.close mdns)
                 (alter jam-service conj {:mdns-server nil}))
               (catch Exception e (println "Exception cleaning up mDNS" e)))
          (try (when-let [tcp-proxy (get @jam-service :tcp-proxy)]
                 (.exit tcp-proxy)
                 (alter jam-service conj {:tcp-proxy nil}))
               (catch Exception e (println "Exception cleaning up TCPMemProxy" e)))))

(defn -startjamsession
  "Starts the jam session server (a thin wrapper around the TCPMemProxy from
    the Jelly test suite).  This call will block until the service is up."
  ([]     (-startjamsession 65456))
  ([port] (let [hostname (.getHostName (java.net.InetAddress/getLocalHost))
                nom      (first (clojure.string/split hostname #"\."))]
            (-startjamsession port (str "jamsession-" nom))))
  ([port service-name]
    (if (serving-jam?)
        (do (println "jamsession already running")
            false)
        (do (println "jamsession starting up on port"  port "...")
            (let [tcp-proxy   (TCPMemProxy. port)
                  ; spawn a long-running thread where tcp server will run
                  _           (future (.run tcp-proxy))
                  ; spawn a brief thread where mDNS will start up
                  mdns-server (future (make-mdns-announcer port service-name 2))
                  ]
              (dosync (alter jam-service conj {:tcp-proxy tcp-proxy})
                      ; deref the future to block here til mDNS setup finishes
                      (alter jam-service conj {:mdns-server @mdns-server})
                      ))
            (println "jamsession started.")
            true))))

(defn -stopjamsession
  "Stops the jam session, if any.  Blocks until the service is down."
  []
  (if (serving-jam?)
      (do (println "jamsession stopping.")
          (shutdown-services))))

;; to catch ctrl-c & other stoppage
(.addShutdownHook (Runtime/getRuntime)
  (proxy [Thread] []
      (run [] (-stopjamsession))))


;; we have a main so that the jarfile can be run standalone
(defn -main [& args]
  (let [[options extra banner]
          (tools/cli args ["-p" "--port" "Listen on this port" :default 65456
                            :parse-fn #(Integer. %)]
                          ["-h" "--help" "Show help" :default false :flag true]
                          ["-v" "--[no-]verbose" :default false])]
    (println "got options" options)
    (if (:help options)
        (println banner)
        (do (when (:verbose options)
                  (.setProperty (System/getProperties) "jamsession.debug" "true"))
            (-startjamsession (:port options))
            ))))

