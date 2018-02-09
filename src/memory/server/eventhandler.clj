(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.game :as game]))

(defn create-game [uid]
    (games/add-new-game uid))

(defn mulitcast-current-game-to-all-its-players [game-id]
  (let [{players :players :as game} (:game-id @games/games)]
        (doseq [uid (vals players)]
            (websocket/chsk-send! uid [:game/play game]))
