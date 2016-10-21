(ns pea.version)

(def version (-> (slurp "project.clj") read-string (nth 2)))
