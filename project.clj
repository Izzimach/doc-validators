(defproject doc-validators "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :main ^:skip-aot doc-validators.core
  :global-vars {*assert* true}

  :target-path "target/%s"
  :profiles {
             :uberjar  {:aot :all}

             ;; run 'lein with-profile +noassert ....' to disable asserts
             :noassert {:global-vars {*assert* false}}
             })
