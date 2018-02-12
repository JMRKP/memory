(ns memory.client.core
    (:require
      [memory.client.communication :as communication]
      [reagent.core :as reagent]
      [re-frame.core :as rf]
      [clojure.string :as str]
      [memory.client.communication :as communication]))

;; -- Domino 1 - Event Dispatch -----------------------------------------------

;; -- Domino 2 - Event Handlers -----------------------------------------------

(rf/reg-event-db              ;; sets up initial application state
  :initialize                 ;; usage:  (dispatch [:initialize])
  (fn [_ _]                   ;; the two parameters are not important here, so use _
    {:game
      (atom
        {:active-player 1
         :deck  [ {:id 0 :url "http://cdn.kickvick.com/wp-content/uploads/2014/11/cute-baby-animals-39.jpg" :turned false :resolved 0}
                  {:id 1 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-31.jpg" :turned false :resolved 0}
                  {:id 2 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-10.jpg" :turned false :resolved 0}
                  {:id 3 :url "https://static.geo.de/bilder/28/52/60898/galleryimage/01-baby-faultiere-kermie.jpg" :turned false :resolved 0}
                  {:id 4 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-31.jpg" :turned false :resolved 0}
                  {:id 5 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-13.jpg" :turned false :resolved 0}
                  {:id 6 :url "https://winkgo.com/wp-content/uploads/2015/02/29-Tiny-Baby-Animals-so-Cute-They-Will-Take-Your-Cares-Away-01.jpg" :turned false :resolved 0}
                  {:id 7 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-13.jpg" :turned false :resolved 0}
                  {:id 8 :url "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTC2b6U3sUWG3XWf0o-rCRN7KXhF9xvPAbBFht-gTsq8r1m1LeRug" :turned false :resolved 0}
                  {:id 9 :url "https://winkgo.com/wp-content/uploads/2015/02/29-Tiny-Baby-Animals-so-Cute-They-Will-Take-Your-Cares-Away-01.jpg" :turned false :resolved 0}
                  {:id 10 :url "http://media.einfachtierisch.de/thumbnail/600/0/media.einfachtierisch.de/images/2013/01/Junge-Katze-Erziehen.jpg" :turned false :resolved 0}
                  {:id 11 :url "https://static.boredpanda.com/blog/wp-content/uuuploads/cute-baby-animals/cute-baby-animals-10.jpg" :turned false :resolved 0}
                  {:id 12 :url "http://cdn.kickvick.com/wp-content/uploads/2014/11/cute-baby-animals-39.jpg" :turned false :resolved 0}
                  {:id 13 :url "https://static.geo.de/bilder/28/52/60898/galleryimage/01-baby-faultiere-kermie.jpg" :turned false :resolved 0}
                  {:id 15 :url "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTC2b6U3sUWG3XWf0o-rCRN7KXhF9xvPAbBFht-gTsq8r1m1LeRug" :turned false :resolved 0}
                  {:id 16 :url "http://media.einfachtierisch.de/thumbnail/600/0/media.einfachtierisch.de/images/2013/01/Junge-Katze-Erziehen.jpg" :turned false :resolved 0}
      ]})}))    ;; so the application state will initially be a map with two keys


(rf/reg-event-db                ;; usage:  (dispatch [:time-color-change 34562])
  :turn-card             ;; dispatched when the user enters a new colour into the UI text field
  (fn [db [_ id]]
    (let [deck  (:deck @(db :game))
          index (.indexOf (vec (map :id deck)) id)]
    (assoc db :game (atom (update-in @(db :game) [:deck index] assoc :turned true))))))  ;; compute and return the new application state

(rf/reg-event-db
  :start-game
  (fn []
    (let [game-id (communication/create-game)])
    (print "create-game")

    ))

;(defn toggle [id] (swap! todos update-in [id :done] not))

;; -- Domino 4 - Query  -------------------------------------------------------

(rf/reg-sub
  :game
  (fn [db _]     ;; db is current app state. 2nd unused param is query vector
    (:game db))) ;; return a query computation over the application state


;; -- Domino 5 - View Functions ----------------------------------------------

(defn card-item-open []
 (fn [{:keys [id, url]}]
   [:li {:on-click #(rf/dispatch [:turn-card id])}
     [:img {:src url}]]))

(defn card-item-closed []
 (fn [{:keys [id]}]
   [:li {:on-click #(rf/dispatch [:turn-card id])}]))

(defn join-game []
       (let [game-id (atom nil)] (fn []
       [:div "Join Game"
         [:form
            [:input {:value @game-id
                    :type "text"
                    :on-change #(reset! game-id (-> % .-target .-value))}]
            [:button {:type "button"
                     :name "join"
                     :onClick #(communication/join-game @game-id)}
                     "Join Game!"]]
          [:div @game-id]]
      )))

(defn gameboard []
   (let [items @@(rf/subscribe [:cards])]
     [:div#gameboard
       [:ul#card-list {:style {:width "600px"}}
         (for [card items]
             ^{:key (:id (val card))} [card-item (val card)])]]))

(defn card-item [card]
 (fn [{:keys [turned]}]
   (if (true? turned)
     [card-item-open card]
     [card-item-closed card])))

(defn gameboard []
   (let [items (:deck @@(rf/subscribe [:game]))]
     [:div#gameboard
       [:ul#card-list {:style {:width "600px"}}
         (for [card items]
             ^{:key (:id card)} [card-item card])]]))

(defn start-view []
  [:div#start-view
    [:button {:on-click #(rf/dispatch [:start-game])} "Start Game" ]
    [:button "Join Game"]])

(defn main-view []
  [:div#main-view
    [:h1 "Memory"]
    [start-view]])

;; -- Entry Point -------------------------------------------------------------

(defn ^:export run
  []
  (rf/dispatch-sync [:initialize])     ;; puts a value into application state
  (reagent/render-component [main-view]              ;; mount the application's ui into '<div id="app" />'
                  (. js/document (getElementById "app"))))
