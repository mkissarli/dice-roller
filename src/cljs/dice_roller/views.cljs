(ns dice-roller.views
  (:require
   [re-frame.core :as re-frame]
   [dice-roller.subs :as subs]
   [reagent.core :as reagent]
   ))

(defn delete-button
  "Button that deletes the effect at item-id in db:effects."
  [item-id]
  [:div.delete-button
   {:on-click #(re-frame/dispatch [:delete-effect item-id])}
   [:i.fas.fa-trash.fa-1x]])

(defn effects
  "Displays a list of effects from db:effects, with a button for deleting each one."
  []
  (let [effects (re-frame/subscribe [::subs/effects])]
    [:div#effects
     [:div#effects-header.grid.grid-col-2
      [:h3.effect "Effect"]
      [:h3.die "Die"]]
     (for [e @effects]
         [:div#single-effect.grid.grid-cols-3
          {:key (get e :key)}
          [:div.effect (get e :effect)]
          [:div.die (get e :die)]
          [:div (delete-button (get e :key))]])]))

(defn atom-input
  "Input with type type, and a reference value of value."
  [value type id]
  [:input.input-basic.focus:outline-none.focus:shadow-outline
   {:type type
    :value @value
    :id id
    :on-change #(reset! value (-> % .-target .-value))}])

(defn add
  "View to add a new effect x on die roll y to db:effects, using a form."
  []
  (let [effect (reagent/atom "") die (reagent/atom "")]
    [:div.grid
     [:h2 "What effects would you like to add to the list?"]
     [:div.grid.sm:grid-cols-3.gap-4
      [:div.grid.sm:grid-cols-2
       [:label
        {:for "input-die-for-effect"}
        "Die: "]
       [atom-input die "text" "input-die-for-effect"]]
      [:div.grid.sm:grid-cols-2
       [:label
        {:for "input-effect-to-add"}
        "Effect: "]
       [atom-input effect "text" "input-effect-to-add"]]
      [:button.btn.btn-blue
       {:on-click #(do
                     (re-frame/dispatch [:add-effect @die @effect])
                     (reset! die "")
                     (reset! effect ""))}
       "Add new!"]]]))


(defn roll-dice
  "Rolls x dice with number of sides y."
  [x y]
  (map inc (repeatedly x #(rand-int y))))

(defn dice-roll
  "View to roll x dice of y sides, handled using a form. On dice roll, sets the
  new activated-effects in db:activated-effects."
  []
  (let [die (reagent/atom 6)
        num-of-rolls (reagent/atom 1)
        dice-rolled (reagent/atom ())
        activated-effects (re-frame/subscribe [::subs/activated-effects])]
    [:div.grid.grid-cols-1
     [:h2 "What dice combination do you want to roll?"]
     [:div.grid.sm:grid-cols-3.gap-4
      [:div.grid.sm:grid-cols-2
       [:label
        {:for "input-die-to-roll"}
        "Die: "]
       [atom-input die "number" "input-die-to-roll"]]
      [:div.grid.sm:grid-cols-2
       [:label
        {:for "input-num-die-roll"}
        "Number of rolls: "]
       [atom-input num-of-rolls "number" "input-num-die-roll"]]
      [:button.btn.btn-blue
       {:on-click
        #(do
           (reset! dice-rolled
                   (roll-dice (js/parseInt @num-of-rolls)
                              (js/parseInt @die)))
           (re-frame/dispatch [:get-activated-effects @dice-rolled]))}
       "Roll dice." ]]
     [:p @dice-rolled]
     ]))

(defn activated-effects
  "A view to display all the activated-effects in db:activated-effects."
  []
  (let [activated-effects (re-frame/subscribe [::subs/activated-effects])]
    (js/console.log @activated-effects)
    [:div#activated-effects
     (for [effect @activated-effects]
       [:div
        {:key (str "ae-" (get effect :key))}
        (get effect :effect)])]))

(defn main-panel
  "The main panel for the page, which includes the components for activated
  effects, added effects, and the forms for adding new effects and rolling
  dice."
  []
  (let [a-e (re-frame/subscribe [::subs/activated-effects])]
    [:div
     ;; Hiccup complains about classes with a '/' so theres this now.
     {:class "container mx-auto px-4 w-full md:w-1/2 lg:w-1/4"}
     [:h1 "A foursouls idea."]
     (if (not (empty? @a-e))
         [activated-effects])
     [effects]
     [:div
      [add]
      [dice-roll]]]))
