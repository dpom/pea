(defproject pea "0.2-dev-01"
  :description "A Personal Education Assistant inside your chat."

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
 :dependencies [[org.clojure/clojure "1.7.0"],
                 [org.clojure/tools.nrepl "0.2.12"]
                 [yetibot.core "0.3.16"]

                 ; logging
                 [devth/timbre "3.3.1"]

                 ; http
                 [clj-http "0.7.7"]
 
                 ; web
                 [compojure "1.4.0"]

                 ; utils
                 [clj-stacktrace "0.2.8"]
                 [useful "0.8.3-alpha8"]
                 [org.clojure/tools.cli "0.3.1"]
                 [environ "1.0.2"]

                 ; NLP
                 [clojure-opennlp "0.3.2"]

                 ]

  :plugins [[lein-exec "0.3.5"]
            [lein-cloverage "1.0.7-SNAPSHOT" :exclusions [org.clojure/clojure]]
            [lein-ring "0.9.5"]
            [io.sarnowski/lein-docker "1.1.0"]]

  :aliases {"version" ["exec" "-ep" "(use 'pea.version)(print version)"]}
  
  :pedantic :ignore

  :profiles {:check {:global-vars {*warn-on-reflection* true}}
             :dev {:source-paths ["dev"]
                   }
             :test {}
             :plugins [[lein-git-deps "0.0.1-SNAPSHOT"]]}

  :repl-options {:init-ns pea.repl
                 :welcome (println "Welcome to the pea development REPL!")}

  :jvm-opts ["-server" "-Xmx2G"]
 
  :docker {:image-name "dpom/pea"}

  :ring {:handler pea.handler/app
         :init    pea.handler/init
         :destroy pea.handler/destroy
         :uberwar-name "pea.war"}

  :main pea.init
)
