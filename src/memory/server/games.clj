(ns memory.server.games
  (:require
    [clojure.java.io :as io]))
(require 'digest)
(def users (atom {}))
(def games (atom {}))

(defn get-sibling-of-card [id]
   (if (odd?)
    (dec id)
    (inc id)))

(defn match? [card-one card-two]
   (= card-two (get-sibling-of-card card-one)))

;; too long TODO: not random - same value always generates same id?
(defn create-game-id [uid]
     (digest/md5 uid))

(defn load-deck-files[]
  (def directory (clojure.java.io/file  "./resources/public/assets"))
  (def files
    (for [file (file-seq directory)]
    (when (.isFile file)
      (.getPath file))))
  (def files-clean (remove nil? files))
files-clean)

(defn create-deck-vector[]
  (def deck-list
    (for [file (load-deck-files)]
      { :id (str (java.util.UUID/randomUUID))
        :url file
        :turned false
        :resolved 0}))
  (def deck-vector
    (into [] deck-list))
    deck-vector)

(defn create-deck[]
      (def deck (into [] (concat (create-deck-vector) (create-deck-vector))))
      (def deck-shuffled (shuffle deck))
      deck-shuffled)

(defn player-nil? [player-key game-id]
  (nil? (first
    (vals (select-keys
        (get (get @games game-id)
        :players)
        [player-key])))))

(defn add-player-to-game [uid game-id]
  (if (nil? (get @games game-id))
    (throw (Exception. "Game does not exist."))
    (if (player-nil? 1 game-id)
      ((swap! games assoc-in [game-id :players 1] uid)
      (swap! users assoc-in [uid] game-id))
      (if (player-nil? 2 game-id)
        ((swap! games assoc-in [game-id :players 2] uid)
         (swap! users assoc-in [uid] game-id))
         (throw (Exception. "There are already two players participating in this game."))))))

(defn create-new-game [player-one-uid]
    {
     :players {1 player-one-uid 2 nil}
     :active-player 1
     :deck (create-deck)})

;;does this append the single elements or append the whole map?
(defn add-new-game [uid]
  (let [game-id (create-game-id uid) game (create-new-game uid)]
    (swap! games assoc-in [game-id] game)
    (swap! users assoc-in [uid] game-id)
    game-id
   ))
