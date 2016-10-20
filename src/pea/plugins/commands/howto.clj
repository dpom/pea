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

;; aux function
(def title_sep "-----")
(def item_sep "********************************************************")

(defn format-howto
  "Prety format the howto,
  @param - the howto map
  @return the howto as string"
  [htmap]
  (let [{bid :howto/bid title :howto/title text :howto/text} htmap]
    (format "[%s] %s\n%s\n%s\n%s\n" bid title
            title_sep
            text
            item_sep)))

;; howto commands

(defn howto-cmd
  "howto <bid> # display the howto with the id bid"
  {:yb/cat #{:util}}
  [{:keys [args]}]
  (let [bid (first (s/split (s/trim args) #"\s"))
        res (ht/get-howto-by-bid bid)]
    (if (nil? res)
      (format "No howtos find for %s\n" bid)
      (format-howto res))))

(deftest howto-cmd-test
  (is (=  (str "[admin101] (Linux) Find The Bigest Dirs\n"
               title_sep  "\n"
               "On linux shell:\ndu -Sk | sort -nr | head -n10 \n"
               item_sep "\n")
          (howto-cmd {:args "admin101"}))
      "bid ok")
  (is (=  "No howtos find for cucu\n"
          (howto-cmd {:args "cucu"}))
      "bid nok")
  )


(defn qht-cmd
  "howto? <wordkey1> [<wordkey2> [<wordkey3]] # search in howtos records that matches all the keys and display those howtos"
  {:yb/cat #{:util}}
  [{:keys [args]}]
  (let [wkeys (s/split args #"\s" 3)
        res (apply ht/get-howto wkeys)]
    (if (empty? res)
      (format "No howtos find for %s\n" args)
      (s/join "\n" (map format-howto res)))))

(deftest qht-cmd-test
  (is (=  (str "[admin101] (Linux) Find The Bigest Dirs\n"
               title_sep  "\n"
               "On linux shell:\ndu -Sk | sort -nr | head -n10 \n"
               item_sep "\n")
          (qht-cmd {:args "linux bigest find"}))
      "3 words")
  (is (=  (str "[admin101] (Linux) Find The Bigest Dirs\n"
               title_sep  "\n"
               "On linux shell:\ndu -Sk | sort -nr | head -n10 \n"
               item_sep "\n\n" 
               "[admin102] (Linux) Limit the cpu usage of a process.\n"
               title_sep "\n"
               "This will limit the average amount of CPU process with the <pid> consumes (on linux shell):\n sudo cpulimit -p <pid> -l 50\n"
               item_sep "\n")
          (qht-cmd {:args "linux"}))
      "1 word")
  (is (=  "No howtos find for cucu bau bau\n"
          (qht-cmd {:args "cucu bau bau"}))
      "empty search")
  )

(cmd-hook #"howto"
          _ howto-cmd)

(cmd-hook #"qht"
          _ qht-cmd)
