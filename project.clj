(defproject doc-validators "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  
  :dependencies [[org.clojure/clojure "1.7.0-alpha1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [prismatic/schema "0.4.3"]
                 [com.velisco/herbert "0.6.11"]]
  
  :main ^:skip-aot doc-validators.core
  
  :global-vars {*assert* true}

  :target-path "target/%s"
  
  :profiles {
             :uberjar  {:aot :all}

             ;; invoke 'lein with-profile +noassert run' to disable asserts
             :noassert {:global-vars {*assert* false}}
             })
