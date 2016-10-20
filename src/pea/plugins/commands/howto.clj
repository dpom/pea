(ns pea.plugins.commands.howto
  (:require
   [yetibot.core.hooks :refer [cmd-hook]]
   [clojure.string :as s]
   [clojure.tools.logging :as log]
   [pea.models.howto :as ht]
   [pea.system :as sys]
   [clojure.test :refer :all]
   ))


;; for tests
(use-fixtures :once
  (fn [tests]
    (println "============== start command howto tests")
    (sys/start-dbsystem!)
    (tests)
    (sys/stop!)
    (println "============== stop command howto tests")
    ))


;; howto command

(defn howto-cmd
  "howto <wordkey1> [<wordkey2> [<wordkey3]] # search in howtos records that matches all the keys and display those howtos"
  {:yb/cat #{:util}}
  [{:keys [args]}]
  (let [wkeys (s/split args #"\s" 3)
        res (apply ht/get-howto wkeys)]
    (if (empty? res)
      (format "No howtos find for %s\n" args)
      (s/join "\n"
              (map (fn [r]
                     (format "%s\n-----\n%s\n" (:howto/title r) (:howto/text r)))
                   res)))))


(deftest howto-cmd-test
  (is (=  "(Linux) Find The Bigest Dirs\n-----\nOn linux shell:\ndu -Sk | sort -nr | head -n10 \n"
          (howto-cmd {:args "linux bigest find"}))
      "3 words")
  (is (=  "(Linux) Find The Bigest Dirs\n-----\nOn linux shell:\ndu -Sk | sort -nr | head -n10 \n\n(Linux) Limit the cpu usage of a process.\n-----\nThis will limit the average amount of CPU process with the <pid> consumes (on linux shell):\n sudo cpulimit -p <pid> -l 50\n"
          (howto-cmd {:args "linux"}))
      "1 word")
  (is (=  "No howtos find for cucu bau bau\n"
          (howto-cmd {:args "cucu bau bau"}))
      "empty search")
  )







(cmd-hook #"howto"
          _ howto-cmd)
