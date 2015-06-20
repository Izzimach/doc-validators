(ns doc-validators.sec3-schema
  (:require [miner.herbert :as h]
            [doc-validators.sec2-destructuring :refer [forward-ballistic]]))

;;
;; simple matching
;;

(h/conforms? '[int str str kw] [3 "test" "Argh" :thing])
(h/conforms? '[int str str kw] [3 "test" "Argh" 'thing])

;;
;; or allows several kinds of matching

(h/conforms? '[int str str] [3 "argh" "test"])
(h/conforms? '[(or int str)] [3 "argh" "test" 5])
(h/conforms? '[(or int str)] [ "argh"  3 6])

;;
;; * ? and + work similar to regexes
;;
;; A*   => zero or more A's
;; A+   => one or more A's
;; A?   => zero or one A's
;;
;; (* A B C) => repeating cycle of types
;;


(h/conforms? '[str int*] ["text"]) ;;true
(h/conforms? '[str int+] ["text"]) ;;false, int+ means "one or more ints"


(h/conforms? '[str int*] ["text" 4]) ;;true
(h/conforms? '[str int*] ["text" 4 5]) ;; true
(h/conforms? '[str int*] ["text" "test" 4 5]) ;; false

(h/conforms? '[str int? kw] ["text" :lolz]) ;;true
(h/conforms? '[str int? kw] ["text" 3 :lolz]) ;;true
(h/conforms? '[str int? kw] ["text" 3 4 :lolz]) ;;false

(def alternating-int-str '[(+ int str)])

(h/conforms? alternating-int-str [3 "text" 5 "stuff"]) ;; true
(h/conforms? alternating-int-str [3 "text" "stuff"]) ;; false
(h/conforms? alternating-int-str [3]) ;; false

;;
;; can use regexes on strings, symbols, or keywords
;;

(def map-with-gnarly-keywords '{(kw ":gnarly-.+") str})

(h/conforms? map-with-gnarly-keywords {:gnarly-a "test" :gnarly-brap "blargh"})
(h/conforms? map-with-gnarly-keywords {:a "test" :gnarly-brap "blargh"}) ;; keyword doesn't start with 'gnarly-'
(h/conforms? map-with-gnarly-keywords {:gnarly-a "test" :gnarly-brap 3}) ;; value is not a string

;;
;; for maps ? indicates optional key
;;

(def a-b-maybe-c '{:a int :b str :c? kw})

(h/conforms? a-b-maybe-c {:a 3 :b "test" :c :lolz}) ;; true
(h/conforms? a-b-maybe-c {:a 3 :b "test"}) ;; true
(h/conforms? a-b-maybe-c {:a "test" :b 4.5 :c 3}) ;; false, a and b are wrong type

(h/conforms? a-b-maybe-c {:a 3 :b "test" :d :lolz}) ;; true! extra keys ignored
(h/conforms? a-b-maybe-c {:a 3 :b "test" :c 3}) ;; false, c is present but is wrong type

;;
;; you can capture values and use them later in the expression
;;

(def two-matching-values '[(:= x num) x])

(h/conforms? two-matching-values [1 1]) ;; true
(h/conforms? two-matching-values [1 2]) ;; false

;; apply constraints using captured data

(def size-then-data '[int [num*]])


(h/conforms? size-then-data [3 [1.1 2.05 3.3]]) ;; true
(h/conforms? size-then-data [3 [1.1 2.05 3.2 4.51 5.99 6.1]]) ;; true, but data is the wrong size!

(def size-then-data-check-size '[(:= data-size int)
                                 (:= data [num*])
                                 (when (== data-size (count data)))])

(h/conforms? size-then-data-check-size [3 [1.0 2.1 1.999]]) ;; true
(h/conforms? size-then-data-check-size [3 [1 2 3 4 5 6]]) ;; false, data size is wrong

;;
;; the conform function will check data and then return bindings
;;

(def parse-data (h/conform size-then-data-check-size))

(parse-data [3 ;; data-size
             [1.0 2.1 2.999] ;; data
             ]) ;; returns values of data-size and data

(parse-data [3 [1.0 2.2 2.222 4]]) ;; fails

;;
;; can write full-fledged grammars if you like!
;;

(def graph-pattern '(grammar
                     ;; start symbol
                     graph
                     ;; rules
                     edge [kw kw num?] ;; optional weight
                     vertex [kw num num num]
                     graph [(:= vertices [vertex]) (:= edges [edge])]
                     ))

(def parse-graph-data (h/conform graph-pattern))

(parse-graph-data [
              ;;vertices
              [[:a 0.0 0.0 0.0]
               [:b 1 1 2]
               [:c 1 4 5]]
              ;;edges
              [[:a :b 0.1]
               [:b :c]
               [:c :a]]])

