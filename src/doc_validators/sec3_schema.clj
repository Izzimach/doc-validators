(ns doc-validators.sec3-schema
  (:require [schema.core :as s]
            [doc-validators.sec2-destructuring :refer [forward-ballistic]]))

(def OnlyNumbers s/Int)

;;
;; validate throws exception if the data is non-conforming
;;

(s/validate OnlyNumbers 3)
;;(s/validate OnlyNumbers "argh")

;;
;; check returns nil where the data conforms, and
;; error messages where it does not
;;

(s/check OnlyNumbers 334)
(s/check OnlyNumbers "argh")
(s/check {:a OnlyNumbers :b OnlyNumbers} {:a 3 :b 'oops})
(s/check [s/Int] [1 2 3 "argh"])

;;
;; sequence schema is usually used with seqs that are  all one type
;;

(s/check [s/Int] [1 2 3 4])

;; full form is [one* optional* rest?]

(def TaggedData [(s/one s/Str :label)
                 (s/optional s/Keyword :option)
                 ;; optional "rest" of values
                 ;; note that all previous optional values must be present as well
                 s/Num
                 ])

(s/check TaggedData ["Null data"])
(s/check TaggedData ["Earthquake 1 data" :fast 1.1 1.2 1.3])
(s/check TaggedData ["Earthquake 2 data" 1.1 1.2 :wrong!])
(s/check TaggedData ["wrong?" 1.1 1.2 1.3])
(s/explain TaggedData)

;;
;; maps and records
;;

(def DynamicsBodySchema
  {:px s/Num
   :py s/Num
   :vx s/Num
   :vy s/Num
   :mass s/Num
   :id s/Str
   })

(def DynamicsForwardConfig
  {:gravity s/Num
   :time s/Num})


#_(s/check DynamicsBodySchema
         {:px 0 :py 0 :vx 5 :vy 6 :mass 1 :id "cannonball"})


#_(s/check DynamicsBodySchema
         {:px 0 :py 3 :vx 5 :vy nil :mass 1 :id "cannonball"})

#_(s/validate DynamicsBodySchema
         {:px 0 :pyy 3 :vx 5 :vy 6 :mass 1 :id "cannonball"})

(s/check DynamicsBodySchema
         {:px 0 :pyy 3 :vx 5 :vy 6 :mass 1 :id "cannonball"})



;;
;; schema's defrecord doesn't check constructors. instead it
;; provides a datatype that can be used in check & validate
;;

(s/defrecord DynamicsBody
    [px   :- s/Num
     py   :- s/Num
     vx   :- s/Num
     vy   :- s/Num
     mass :- s/Num
     id   :- s/Str])

(DynamicsBody. 0 0 5 6 3 "argh")
(DynamicsBody. 0 0 5 6 "argh" 3) ;; doesn't fail! you still need to check records
(->> (DynamicsBody. 0 0 5 6 "argh" 3)
     (s/check DynamicsBody))

;;
;; you can print out the schema definition if you need it
;;

(s/explain DynamicsBody)



;;
;; functions can have 'types' attached that typecheck the parameters passed in and
;; the return value
;;

(s/defn addnumbers :- s/Num
  [nums :- [s/Num]]
  (apply + nums))

(addnumbers [1 2 3])
#_(addnumbers [1 2 "argh"]) ;; doesn't auto validate...
#_(s/with-fn-validation (addnumbers [1 2 "argh"]))

;;
;; defrecord types are valid as well
;;

(s/defn schema-ballistic :- DynamicsBody
  [dataattime0 :- DynamicsBody
   config :- DynamicsForwardConfig]
  (let [{:keys [px py vx vy mass id]} dataattime0
        {:keys [gravity time]}        config]
    (forward-ballistic px py vx vy mass id time gravity)))

#_(s/with-fn-validation
  (let [startbody (DynamicsBody. 0 0 5 6 1 "argh")]
    (schema-ballistic startbody {:gravity -1 :time 2}))) ;; wrong output!

;;
;; forward-ballistic returns a map; convert into a DynamicsBody record
;; before returning...
;;

(s/defn schema-ballistic-fixed :- DynamicsBody
  [dataattime0 :- DynamicsBody
   config :- DynamicsForwardConfig]
  (let [{:keys [px py vx vy mass id]} dataattime0
        {:keys [gravity time]}        config]
    (-> (forward-ballistic px py vx vy mass id time gravity)
        map->DynamicsBody)))

#_(s/with-fn-validation
  (let [startbody (DynamicsBody. 0 0 5 6 1 "argh")]
    (schema-ballistic-fixed startbody {:gravity -1 :time 2})))

;;
;; function validation is controlled by *assert*, just like normal :pre and :post metadata
;;

;;
;; Other things available for more sophisticated data types: optional, either, if, etc.
;;
