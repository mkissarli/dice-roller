(ns dice-roller.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn -main
  "I don't do a whole lot...yet."
  [& args]
  (println "Hello, World!"))

(defn roll-dice
  "Rolls x dice with number of sides y."
  [x y]
  (repeatedly x #(rand-int y)))

(defn add-conditional-value
  "Adds a new dice value x, with an effect y, as a hashmap to a list of other hashmaps z"
  [x y z]
  (assoc z x (conj (get z x) y)))
  
(defn effects-activated
  "Checks a list of hashmaps x for values in list y, displaying any other values in their list"
  [x y]
  (flatten
   (vals
    (select-keys x y))))

(defn user-input
  "The main loop for the app."
  [effects-map]
  (println effects-map)
  (println "Press 1 to add an effect and 2 to roll a new set of dice.")
  (let [choice (str (read-line))]
    (if (= choice "1")
      (do
        (println "Which dice value triggers the effect?")
        (let [val (Integer/parseInt (read-line))]
          (println "And whats the effect?")
          (let [effect (str (read-line))]
            (println "Thanks for that. We've updated your effects list.")
            (user-input (add-conditional-value val effect effects-map))))
        )
      (println "Coming soon")
      )))
