(defproject fb-graph-api "0.1.0-SNAPSHOT"
  :description "Clojure binding for Facebook Graph API"
  :url "http://github.com/borovsky/fb-graph-api"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :jvm-opts ^:replace ["-Xmx1g"
                       "-server" ;"-Dcfj-env=prod"
                       ]

  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [org.clojure/tools.logging "0.3.1" :exclusions [org.clojure/clojure]]
                 [http-kit "2.1.18" :exclusions [org.clojure/clojure]]
                 [org.clojure/data.json "0.2.6"]

                 ;;; Testing
                 [midje "1.6.3" :exclusions [joda-time
                                             slingshot
                                             org.clojure/clojure]]

                 ;;; Locking transient deps
                 [commons-codec/commons-codec "1.10"]
                 [slingshot "0.12.2" :exclusions [org.clojure/clojure]]
                 [clj-time "0.9.0" :exclusions [org.clojure/clojure]]
                 [org.clojure/math.combinatorics "0.1.1"]
                 [net.cgrand/parsley "0.9.3" :exclusions [org.clojure/clojure]]]
  :dev-dependencies []

  :plugins [[lein-ancient "0.6.7" :exclusions [commons-codec]]
            [jonase/eastwood "0.2.1"]
            [lein-deps-tree "0.1.2" :exclusions [commons-codec
                                                 org.codehaus.plexus/plexus-utils]]
            [lein-midje "3.1.3"]
            [lein-marginalia "0.8.0"]
            [cider/cider-nrepl "0.9.0-SNAPSHOT"]
            [lein-kibit "0.1.2"]
            [lein-bikeshed "0.2.0" :exclusions [org.clojure/tools.namespace]]
            [refactor-nrepl "1.1.0-SNAPSHOT"]]

  :aliases {"omni" ["do"
                    ["clean"]
                    ["with-profile" "production" "deps" ":tree"]
                    ["ancient"]
                    ["kibit"]
                    ["bikeshed"]]})
