(ns doc-validators.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "I'm gonna assert!")
  (assert false)
  (println "I asserted!"))

;;
;; asserts throw if the first argument evaluates to false
;; the second argument is an optional message to print

(assert true)
;;(assert false)

(assert (number? 4) "This should be a number")
;;(assert (number? :x) "This should be a number")

;;
;; disable asserts by setting *assert* to false
;;

(set! *assert* true)
(set! *assert* false)

;;
;; note that assert is a macro so *assert* will be checked at
;; macro expansion time, not run time
;;

;;
;; function pre and post conditions are asserts that fire at the start/end of functions
;; each is an array of statements that must all evaluate to true
;;

(defn gcd [a b]
  {:pre [(pos? a)
         (number? b)
         ;; (pos? b) ;; b can be zero, so don't use this
         ]
   :post [(pos? %)]}
  (let [[a' b'] (cond
                  (> a b) [a b]
                  :else [b a])]
    (if (= 0 b')
      a'
      (gcd b' (- a' b')))))

(gcd 219 123)
