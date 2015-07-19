(set-env!
 :source-paths #{"src"}
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure "1.7.0" :scope "provided"]
                 
                 [adzerk/bootlaces "0.1.9" :scope "test"]
                 [jeluard/boot-notify    "0.2.0"      :scope "test"]
                 [zilti/boot-midje "0.1.2"   :scope "test"]
                                        ; Backend
                 [http-kit "2.1.19" :exclusions [org.clojure/clojure]]
                 [org.clojure/data.json "0.2.6"]

                 ;;; Testing
                 [midje "1.7.0" :exclusions [joda-time
                                             slingshot
                                             org.clojure/clojure] :scope "test"]

                 ;;; Locking transient deps
                 [commons-codec/commons-codec "1.10"]
                 [slingshot "0.12.2" :exclusions [org.clojure/clojure]]
                 [clj-time "0.10.0" :exclusions [org.clojure/clojure]]
                 [org.clojure/math.combinatorics "0.1.1"]
                 [net.cgrand/parsley "0.9.3" :exclusions [org.clojure/clojure]]
                 ])

(require
 '[adzerk.bootlaces :refer :all]
 '[jeluard.boot-notify :refer [notify]]
 '[zilti.boot-midje :refer [midje]])

(def +version+ "0.1.1-SNAPSHOT")
(bootlaces! +version+)

(task-options!
 pom {:project 'fb-graph-api
      :version +version+
      :description "Clojure binding for Facebook Graph API"
      :license {"name" "Eclipse Public License"
                "url" "http://www.eclipse.org/legal/epl-v10.html"}}
 midje {:test-paths #{"test"}})

(deftask dev
  "Run a restartable system in the Repl..."
  [n notify-enabled       bool "Notify when build is done"]
  (comp
   (watch :verbose true)
   (midje)
   (if notify-enabled (notify) identity)
   (repl :server true))))
