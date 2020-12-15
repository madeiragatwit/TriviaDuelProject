# Trivia Duel
## Introduction
Trivia Duel is an online multiplayer game in which players correctly answer more questions than each other.

## Game Features
- Create individual game rooms and invite other players to join with a given **room code**.
- Once game is started by room leader (player to create room), answer questions and get points. (First player to 10 points wins game.)
- If multiple players win the game (reach 10 points), all corresponding players' names will be presented in the winning result.

## Installation
### Download
- [Download the game here.](http://bit.ly/3mjL6mf)
### Run
- Unzip **TriviaDuel.zip**, and run **TriviaDuel.exe**
- You may encounter the error: **This application requires a Java Runtime Environment 1.8.0.** You can download this runtime [here.](https://www.oracle.com/java/technologies/javase-jre8-downloads.html)
## Demo Video
[YouTube Link](https://www.youtube.com/watch?v=wAYxfCAk73w)
## Backend Information
URL: http://zenith.blue:8082 // **ALL** requests must require a "k" field within the query. **The API key can be found within Dain Im's Project Team Evaluation Document.**

Routes: 
 - /questions/list
 - /questions/rnd
 - /questions/new -- **SHOULD NOT BE ACCESSED OUTSIDE OF VIEW**
 - /questions/remove -- **SHOULD NOT BE ACCESSED OUTSIDE OF VIEW**
 
 Views: 
 - /
 
 If you're trying to make your own version of this backend, you **must** use the .sql file within the folder to setup the MariaDB properly. Replace API keys within index.js, and /views/input.html, and lastly replace database information according to your DB within index.js. [Installation Guide](http://expressjs.com/en/starter/installing.html)
## Contributors
- Chris D'Entremont - Frontend Developer
- Gabriel Madeira - JavaFX Designer
- Dain Im - Backend Developer
