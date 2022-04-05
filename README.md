### **Game Server**

#### Short notes

##### Known limitations:
* Every connection to server is interpreted like "new player" connection.
* Work with streams and queues is simplified (errros, interruptions).
* Very simple protocol between frontend and backend. 
Therefore UI shows raw json structures and on the backend games are searched by player connected.
There are some additional information in json (not only messages from the task text), hope it's ok.
* Logging via println to console.
* There are no some validations / checks.

##### Decisions made
* Main messages handling logic was placed in domain-related class representing a game
* There were thoughts to make solution more abstract - with introduction of game state and rounds
with each state having it's own configuration of folds / plays costs and logic.
Something like

[
    
    State1 => State1Logic(),
    State2 => State2Logic() ...
]

Also I thought a lot about namings, especially for results logic
* Optional features implemented are: UI part, simple game logic was extended for a number of players 
and more than 2 cards.
* I guess combinations logic should be more complex (considering different combinations number from players hands)

##### Launching
* Standard via sbt. After app started it can be tested in browser (default port 8080). 
You need to enter commands in the bottom text input. Some debug info will be in console
and browser console.  

  

     