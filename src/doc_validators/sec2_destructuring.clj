(ns doc-validators.sec2-destructuring
  (:gen-class))


;;
;; ballistic falling of an object
;;
;; data form represents the position/velocity at time zero:
;;
;; {
;;  :px <initial x position, meters>
;;  :py <initial y position, meters>
;;  :vx <initial x velocity, meters/sec>
;;  :vy <initial y velocity, meters/sec>
;;  :mass <mass in kg>
;;  :id <some id, whatever>
;; }
;;
;; extra parameters:
;; time - compute new position/velocity at this point in time
;; gravity - gravity value to use; normal gravity is negative

(defn forward-ballistic [px py vx vy mass id time gravity]
  {:pre [(number? px)
         (number? py)
         (number? vx)
         (number? vy)
         (number? mass)
         (number? time)
         (number? gravity)]}
  ;;
  ;; standard equation for something being accelerated:
  ;; p_t = p_0 + v_0 * t + 0.5 * accel * t * t
  ;;
  ;; primed values are the new values
  ;;
  (let [px' (+ px (* vx time))
        py' (+ py (* vy time) (* 0.5 gravity time time))
        vx' vx
        vy' (+ vy (* gravity time))]
    {:px px'
     :py py'
     :vx vx'
     :vy vy'
     :mass mass
     :id id}))

(defn destructuring-ballistic
  [{:keys [px py vx vy mass id]} {:keys [gravity time]}]
  {:pre [(not (nil? id))]}
  (forward-ballistic px py vx vy mass id time gravity))

;;
;; with records
;;

(defrecord DynamicsBody [px py vx vy mass id])

;;(destructuring-ballistic (DynamicsBody. 0 0 5 5 1 "argh") {:gravity 1 :time 2})


;;(destructuring-ballistic (map->DynamicsBody {:px 0 :py 0 :vx 5 :vy 5 :mass 1 :id "argh"}) {:gravity 1 :time 2})
