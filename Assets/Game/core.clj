;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;; Game of Life
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns gol)

(defn create-world
  "Creates rectangular world with the specified width and height.
  Optionally takes coordinates of living cells."
  [w h & living-cells]
  (vec (for [y (range w)]
         (vec (for [x (range h)]
                (if (contains? (first living-cells) [y x]) "X" " "))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Unity version
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(use 'arcadia.core 'arcadia.linear)
(import '[UnityEngine GameObject Time Mathf Transform])

(def alive (object-named "Alive"))

(def spawn (atom 0))
(def timeToGo (atom (+ (.. Time fixedTime) 1)))

(defn unitySpawn [x y]
  (instantiate alive
               (v3 (* x 2) 0 (* y 2))
               (qlookat (v3 0 0 0)(v3 0))
  )
)
(defn deadSpawn [x y]
  ;(create-primitive :cube)
  (instantiate (object-named "Dead")
               (v3 (* x 2) 0 (* y 2))
               (qlookat (v3 0 0 0)(v3 0))
  )
)

(defn create-world
  "Creates rectangular world with the specified width and height.
  Optionally takes coordinates of living cells."
  [w h & living-cells]
  (doseq [o (objects-named "Alive(Clone)")](destroy o))
  (doseq [o (objects-named "Dead(Clone)")](destroy o))
  (vec (for [y (range w)]
         (vec (for [x (range w)]
                (if (contains? (first living-cells) [y x]) (unitySpawn x y) (deadSpawn x y)))))))

(defn neighbours
  "Determines all the neighbours of a given coordinate"
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn stepper
  "Returns a step function for Life-like cell automata.
   neighbours takes a location and return a sequential collection
   of locations. survive? and birth? are predicates on the number
   of living neighbours."
  [neighbours birth? survive?]
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbours cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))

; patterns
(def glider #{[2 0] [2 1] [2 2] [1 2] [0 1]})
(def light-spaceship #{[2 0] [4 0] [1 1] [1 2] [1 3] [4 3] [1 4] [2 4] [3 4]})

; steppers
(def conway-stepper (stepper neighbours #{3} #{2 3}))

(defn conway
  "Generates world of given size with initial pattern in specified generation"
  [[w h] pattern iterations]

  (->> 
    (iterate conway-stepper pattern)
    (drop iterations)
    first(create-world w h)
    (map println)
  )
)

(defn orbit [^GameObject obj, k]         ; Takes the GameObject and the key this function was attached with

 (if (>= (.. Time fixedTime) @timeToGo) 
    ;((swap! timeToGo (partial + (+ (.. Time fixedTime) 3)))(conway [10 10] glider @spawn))
    (
      log "yee"
      (swap! timeToGo (partial + 3))
      (swap! spawn (partial + 1))[]
      (conway [10 10] glider @spawn)
    )
    (
      log "not"
    )
 )
  ;(conway [10 10] glider spawn)
  (let [{:keys [:radius]} (state obj k)] ; Looks up the piece of state corresponding to the key `k`
    (with-cmpt obj [tr Transform]
      )))

(let [gobj (create-primitive :cube "Orbiter")]
  (state+ gobj :orbit {:radius 5})          ; set up state
  (hook+ gobj :fixed-update :orbit #'orbit) ; set up message callback (hook)
 )

(for [number [1 2 3]
  letter [:a :b :c]]
  (str number letter))

(for [length [(range 10)]
    width [(range 10)]]
    (str length width))

(vec (for [y (range 10)]
         (vec (for [x (range 10)]
                (str x y)))))

(for [y (range 10)]
         (for [x (range 10)]
                (str x y)))

(time(map (fn [y] 
  (map (fn [x] (str x y))
    [1 2 3]))
    [1 2 3]
))

(map (fn [y] (pmap (fn [x] (unitySpawn x y))[1 2 3]))[1 2 3])

(unitySpawn 0 1)

(def regMap []
  (map 
    (fn [y] (map (fn [x] (unitySpawn x y))
      [1 2 3 4 5])
    )[1 2 3 4 5]
  )
)
