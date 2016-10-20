(defproject pea "0.2-dev-01"
  :description "A Personal Education Assistant inside your chat."

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"],
                 [yetibot.core "0.3.16"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [com.stuartsierra/component "0.3.1"]

                 ;; logging
                 [org.clojure/tools.logging "0.3.1"]
                 [log4j "1.2.17" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]

                 ;; http
                 [clj-http "0.7.7"]

                 ;; web
                 [compojure "1.4.0"]

                 ;; utils
                 [clj-stacktrace "0.2.8"]
                 [useful "0.8.3-alpha8"]
                 [org.clojure/tools.cli "0.3.1"]
                 [environ "1.1.0"]

                 ;; NLP
                 [clojure-opennlp "0.3.2"]

                 ]

  :plugins [[lein-exec "0.3.5"]
            [lein-cloverage "1.0.7-SNAPSHOT" :exclusions [org.clojure/clojure]]
            [lein-ring "0.9.5"]
            [lein-environ "1.1.0"]
            [io.sarnowski/lein-docker "1.1.0"]]

  :aliases {"version" ["exec" "-ep" "(use 'pea.version)(print version)"]}

  :pedantic :ignore

  :env {:app-version :project/version}
  
  :profiles {:check {:global-vars {*warn-on-reflection* true}}
             :dev {:source-paths ["dev"]
                   :test-paths ["src"]
                   :resource-paths ["log"]
                   :jvm-opts ["-Dlog4j.configuration=file:log/log4j.properties"]
                   :env {:config-file "dev/config.edn"}
                   :dependencies [[org.clojure/tools.namespace "0.2.10"]
                                  [org.clojure/tools.reader "0.9.2"]
                                  [org.clojure/java.classpath "0.2.2"]
                                  [org.clojure/tools.trace "0.7.8"]
                                  [ring/ring-devel "1.3.2"]
                                  [alembic "0.3.2"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [pjstadig/humane-test-output "0.7.0"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]
                   }
             :test {:env {:config-file "test/config.edn"}}
             :production {:env {:config-file "config/config.edn"}}
             :plugins [[lein-git-deps "0.0.1-SNAPSHOT"]]}

  :repl-options {:init-ns user
                 :welcome (println "Welcome to the pea development REPL!")}

  :jvm-opts ["-server" "-Xmx2G"]

  :docker {:image-name "dpom/pea"}

  :ring {:handler pea.handler/app
         :init    pea.handler/init
         :destroy pea.handler/destroy
         :uberwar-name "pea.war"}

  :main pea.init
  )
