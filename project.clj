(defproject visdb "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [noir "1.2.1"]
                           [jayq "0.1.0-SNAPSHOT"]
                           [crate "0.1.0-SNAPSHOT"]
                           [fetch "0.1.0-SNAPSHOT"]]
            :cljsbuild {:source-path "src" 
                        :compiler
                          {:output-dir "resources/public/cljs/"
                           :output-to "resources/public/cljs/all.js"
                           :pretty-print true}} 
            :plugins [[lein-cljsbuild "0.2.1"]]
            :main visdb.server)
