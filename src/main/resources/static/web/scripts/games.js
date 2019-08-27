fetch('/api/games/')
    .then((response) => response.json())
    .then((data) => {

    if (data.player !== "guest") {
    document.getElementById("loginForm").style.display = "none";
    document.getElementById("registerForm").style.display = "none";
    } else {
    document.getElementById("logout-form").style.display = "none";
    }
        createGameTable(data);
console.log(data);
    })

fetch('/api/players/')
    .then((response) => response.json())
    .then((data) => {
        displayLeaderboard(data);
        sortLeaderboard();
    })


function createGameTable(games) {
    var displayId = document.getElementById("username")
    var ulGames = document.getElementById("games");
    var loggedInUser = games.player
    var games = games.games

    displayId.innerText = loggedInUser.username

    console.log(games);
        for (var i = 0; i < games.length; i++) {

            var date = games[i].created;
            var players = games[i].gamePlayers;
            var currentPlayers = []
            var listItem, a, text, nn;

            for (var j = 0; j < players.length; j++) {
                currentPlayers.push(players[j].player.username)
            }

            listItem = document.createElement("li");
            a  = document.createElement('a');
            text = document.createTextNode(games[i].created + currentPlayers);
            listItem.appendChild(text);
            ulGames.appendChild(listItem)


for (var k = 0; k < players.length; k++) {
    if (players[k].player.id == loggedInUser.id) {


            nn = players[k].id
            a.href = "game.html?gp=" + nn;
            a.appendChild(text);
            listItem.appendChild(a);
            ulGames.appendChild(listItem)


    }
}

    if ((games[i].gamePlayers.length == 0) || (games[i].gamePlayers.length == 1)) {

    for (var a = 0; a < games[i].gamePlayers.length; a++) {
        if (games[i].gamePlayers[a].player.id !== loggedInUser.id) {
        var gameId = games[i].id
        var buttonString = "Join game!"
        var createGameButton = document.createElement('button');
        var buttonText = document.createTextNode(buttonString)
            createGameButton.appendChild(buttonText);
            createGameButton.setAttribute("id", "joinGame")
            createGameButton.setAttribute("gameId",gameId);
            createGameButton.onclick = function() {
                    joinGame();
                }
            ulGames.appendChild(createGameButton);
        }
      }
    }
  }
}

function displayLeaderboard(players) {

    var tableHeader = ["Name", "Total", "Won", "Lost", "Tied"]
    var playerList = [];
    var leaderboard = document.getElementById("leaderboard");
    var scoreList = [];

// Build the table headers
        for (var i = 0; i < tableHeader.length; i++) {
            var th = document.createElement("th");
            th.setAttribute("id", tableHeader[i] + "header")
            th.textContent = tableHeader[i]
            leaderboard.appendChild(th);
        }

    for (var i = 0; i < players.length; i++) {

        var totalScore = 0;
        var scoreArray = [];
        var row = document.createElement("tr");
        var leaderboardName = document.createElement("td");
        var leaderboardTotal = document.createElement("td");
        var leaderboardWon = document.createElement("td");
        var leaderboardLost = document.createElement("td");
        var leaderboardTied = document.createElement("td");

        leaderboardName.append(players[i].username);

        var wonCounter = [];
        var lostCounter = [];
        var tiedCounter = [];

        for (var j = 0; j < players[i].scores.length; j++) {
            scoreArray.push(players[i].scores[j].score)
            totalScore = scoreArray.reduce((x, y) => x + y);

            if (players[i].scores[j].score === 1){
                wonCounter.push("W");
            }
            if (players[i].scores[j].score === 0) {
                lostCounter.push("L");
            }
            if (players[i].scores[j].score === 0.5) {
                tiedCounter.push("T");
            }

       }

        leaderboardWon.append(wonCounter.length);
        leaderboardLost.append(lostCounter.length);
        leaderboardTied.append(tiedCounter.length);
        leaderboardTotal.append(totalScore);


        row.append(leaderboardName, leaderboardTotal, leaderboardWon, leaderboardLost, leaderboardTied);
        leaderboard.append(row);

    }


}

function sortLeaderboard() {
var switching, shouldSwitch, x, y;

switching = true;

while (switching) {
    switching = false;

    rows = leaderboard.rows

    for (var i = 0; i < 6; i++) {
        shouldSwitch = false;

        x = rows[i].getElementsByTagName("td")[1];
        y = rows[i + 1].getElementsByTagName("td")[1];

        if (Number(y.innerHTML) > Number(x.innerHTML)) {
        shouldSwitch = true;
        break;
      }
    }
    if (shouldSwitch) {
      rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
      switching = true;
    }



    }

}





function joinGame() {
var joinGameButton = document.getElementById("joinGame");
var gameId = joinGameButton.getAttribute("gameId");
var gamePlayerId =

fetch("/api/game/" + gameId + "/players", {
    credentials:'include',
    headers: {
    'Content-Type': 'application/json'
    },
    method: 'POST'})
    .then(function(response) {
    if(response.ok){
        return response.json()
    }
    throw new Error()
     })
    .then(function(data) {
 window.location = "./game.html?gp=" + data.gameId;
     })
    .catch(function(error){
          console.log("Request failure: ", error);
     });
}



function createGame() {

var createButton = document.getElementById("createButton");

fetch("/api/games",{
    credentials:'include',
    headers: {
    'Content-Type': 'application/json'
    },
    method: 'POST'})
    .then(function(response) {
        var failState = document.getElementById("createGameFail");
        if (response.status == 409) {
            alert("Please create an account and log in to make a new game.")
        } else {
            window.location = "./games.html";
            }
        return response.text();
     })
    .then(function(data) {
    return data.text();
     })
    .catch(function(error){
          console.log("Request failure: ", error);
     });
}

function login() {
var name = document.getElementById("loginLabel").value
var pwd = document.getElementById("passwordLabel").value

var ourData = {name, pwd}

    fetch("/api/login",{
        credentials: 'include',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
            },
            method: 'POST',
            body: getBody(ourData)
            })
            .then(function(response) {
            var failState = document.getElementById("failState");
if (response.status == 401) {
failState.style.display = "inline"
} else {
                window.location = "./games.html";
}
                return response.text();
            })
            .then(function(data) {
            })
            .catch(function(error){
                console.log("Request failure: ", error);
            });
}
function getBody(json){
            var body = [];
            for (var key in json) {
               var encKey = encodeURIComponent(key);
               var encVal = encodeURIComponent(json[key]);
               body.push(encKey + "=" + encVal);
            }
         return body.join("&");



}
function logout() {
 fetch("/api/logout", {
        method:'POST',
        })
        .then(function(data) {
        window.location = "./games.html";
        })
        .catch(function(error){
            console.log("Request failure: ", error);
        });


}

function register() {

var username = document.getElementById("loginLabel").value
var password = document.getElementById("passwordLabel").value

var newUser = {username, password}


    fetch("/api/players",{
        headers: {
            'Content-Type': 'application/json'
            },
            method: 'POST',
            body: JSON.stringify(newUser)
            })
            .then(function(response) {
                return response.text();
            })
            .then(function(data) {
            alert(data)
            window.location = "./games.html";
            })
            .catch(function(error){
                console.log("Request failure: ", error);
            });

}

