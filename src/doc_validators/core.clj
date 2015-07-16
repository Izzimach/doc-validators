(ns doc-validators.core
  (:require [doc-validators.sec1-asserts :as sec1]
            [doc-validators.sec2-destructuring :as sec2]
            [doc-validators.sec3-schema :as sec3]
            [doc-validators.sec4-herbert :as sec4])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "I'm gonna assert!")
  (assert false)
  (println "I asserted!"))
