(ns pea.plugins.commands.study
  (:require
   [yetibot.core.hooks :refer [cmd-hook]]
   [clojure.string :as s]
   [clojure.tools.logging :as log]
   [pea.models.study :as stu]
   [pea.system :as sys]
   [clojure.test :refer :all]
   ))


;; for tests
(use-fixtures :once
  (fn [tests]
    (println "============== start command study tests")
    (sys/start-dbsystem!)
    (tests)
    (sys/stop!)
    (println "============== stop command study tests")
    ))

;; aux function
(def title_sep "-----")
(def res_ok "Răspunsul tău este corect. ")
(def res_nok "Răspunsul tău este greșit.")
(def res_syntax "Răspunsul nu poate fi decat 1 sau 2.")
(def no_item "No studies find for %s\n")

(defn format-study
  "Prety format the study,
  @param stumap - the study map
  @param type - item type (true, false or neutral)
  @return the study item as string"
  [stumap type]
  (let [{bid :study/bid text :study/text} stumap]
    (format "%s\n[%s]\n%s\n%s\n"
            (case type
              "true" (str res_ok title_sep "\n")
              "false" (str res_nok title_sep "\n")
              "")
            bid
            title_sep
            text)))

;; study commands

(defn study-cmd
  "study <bid> # display the study with the id bid"
  {:yb/cat #{:util}}
  [{:keys [args]}]
  (let [params (s/split (s/trim args) #"\s")
        bid (first params)]
    (if (empty? bid)
      (format-study (stu/get-study-by-bid "hist101") "neutral")
      (let [res (stu/get-study-by-bid bid)]
        (if (nil? res)
          (format no_item bid)
          (format-study res "neutral"))))))


(defn response-cmd
  "response <bid> <q> # respond with q (1 or 2) to the <bid> study item"
  {:yb/cat #{:util}}
  [{:keys [args]}]
  (let [params (s/split (s/trim args) #"\s" 2)
        bid (first params)
        q (Integer/parseInt (second params))
        next (stu/get-res-next bid q)
        res (stu/get-study-by-bid next)]
    (if (nil? res)
      (format no_item bid)
      (if-not (or (= q 1) (= q 2))
        res_syntax
        (format-study res (stu/get-res-type bid q))))))


(cmd-hook #"study"
          _ study-cmd)

(cmd-hook #"response"
          _ response-cmd)
