;; project settings
(setq ent-project-home (file-name-directory (if load-file-name load-file-name buffer-file-name)))
(setq ent-project-name "pea")
(setq ent-clean-regexp "~$\\|\\.tex$")
(setq ent-project-config-filename "PEA.org")

;; local functions

(defvar project-version)

(setq project-version (ent-get-version))


;; tasks

(load ent-init-file)

(task 'doc '(compile) "build the project documentation" '(lambda (&optional x) "lein doc"))

(task 'format '() "format the project" '(lambda (&optional x) "lein cljfmt fix"))

(task 'check '() "check the project" '(lambda (&optional x) "lein with-profile +check checkall"))

(task 'tree '() "tree dependencies" '(lambda (&optional x) "lein do clean, deps :tree"))

(task 'tests '() "run tests" '(lambda (&optional x) "lein do clean, test"))

(task 'docker '() "build and upload docker image" '(lambda (&optional x) "lein do docker build, docker push"))

(task 'libupdate () "update project libraries" '(lambda (&optional x) "lein ancient :no-colors"))

(task 'package '() "package the library" '(lambda (&optional x) "lein do clean, uberjar"))

(task 'zip '() "package the sources" '(lambda (&optional x) "lein zip"))

;; Local Variables:
;; no-byte-compile: t
;; no-update-autoloads: t
;; End:
