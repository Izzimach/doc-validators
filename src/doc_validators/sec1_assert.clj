(ns doc-validators.sec1-assert
  (:gen-class)
  (:require [clojure.core.async :as async :refer [go chan <! >! >!! <!!]
             clojure.stacktrace :refer [print-stack-trace]]))


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
;;(set! *assert* false)

;;
;; note that assert is a macro so *assert* will be checked at
;; macro expansion time, not run time
;;

;;
;; function pre and post conditions are asserts that fire at the start/end of functions
;; each is an array of statements that must all evaluate to true
;;

(defn gcd [a b]
  {:pre [(number? a)
         (pos? a)
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

(gcd 221 13)

(gcd :k 13)


;;
;; when using channels, be aware that assertions can kill your go block!
;;

(defn do-some-stuff-with-numbers
  [x]
  {:pre [(number? x)]}
  (print (* 2  x)))

(defn only-numbers-channel
  []
  (let [channel (chan)]
    (go (while true
          (let [val (<! channel)]
            #_(do-some-stuff-with-numbers val)
            (try 
              (do-some-stuff-with-numbers val)
              (catch AssertionError e (do (print (.getMessage e))
                                             (print-stack-trace e)))))))
    channel))

(let [x (only-numbers-channel)]
  (>!! x 1)
  (>!! x :k)
  (>!! x 3))
