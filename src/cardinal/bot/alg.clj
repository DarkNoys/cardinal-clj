(ns cardinal.bot.alg)

;;;;;;;;;;;
;; Tools ;;
;;;;;;;;;;;
;;
;; Base algorythm
;;

;;
;; Find near bild pos
;;
(defn get-indicate-can-build
  "return function indicator build in pos"
  [state utype]
  (let [game (state-get-game state)]
    (fn [x y]
      (game-can-build-here game (TilePosition. x y) utype))))
      ;;     (not (unit-in-way? state x y))))))

(defn get-common-build-tile
  "return tile-pos when can build building-type"
  [state building-type around-tile-pos start-dist stop-dist]
  (loop [max-dist start-dist
         pos nil]
    (if-not (and (>= stop-dist max-dist)
                 (empty? pos))
      pos
      (let [x-range (range (- (.getX around-tile-pos) max-dist)
                           (+ (.getX around-tile-pos) max-dist 1))
            y-range (range (- (.getY around-tile-pos) max-dist)
                           (+ (.getY around-tile-pos) max-dist 1))
            new-pos (for [x x-range
                          y y-range
                          :when ((get-indicate-can-build state
                                                         building-type)
                                 x y)]
                      (TilePosition. x y))]
        (recur (+ max-dist 2) new-pos)))))



;; FIX for other building
(defn get-build-tiles
  "return tile-pos when can build building-type"
  [state building-type around-tile-pos]
  (let [start-dist 25
        stop-dist 25
        positions (get-common-build-tile state
                                         building-type around-tile-pos
                                         start-dist stop-dist)
        sorted-pos (sort
                    #(<
                      (tile-position-get-distance
                       around-tile-pos
                       %1)
                      (tile-position-get-distance
                       around-tile-pos
                       %2))
                    positions)]
    (println (tile-position-get-distance around-tile-pos (first positions)))
    positions))

