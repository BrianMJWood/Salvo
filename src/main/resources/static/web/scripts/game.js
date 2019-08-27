createShipTable();
createSalvoTable();
loadShips();

fetch("/api/game_view/" + getGamePlayerId())
    .then((response) => response.json())
    .then((data) => {
console.log(data);
        displayShips(data.ships);
        playerInfo(data);
        displayHits(data);
})


function postShips() {
var postShipsButton = document.getElementById("postShipsButton")
fetch("/api/games/players/" + getGamePlayerId() + "/ships",
        {
        credentials:'include',
        headers: {
        'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify([
                { "type": "destroyer", "locations": ["A1", "B1", "C1"] },
                { "type": "patrol boat", "locations": ["H5", "H6"] },
                { "type": "carrier", "locations": ["E5", "E6", "E7", "E8", "E9"] },
                { "type": "cruiser", "locations": ["J5", "J6", "J7", "J8"] },
                { "type": "frigate", "locations": ["A5", "A6", "A7"] }
              ])
        })
        .then(function(response) {

        if(response.ok){
            location.reload(true)
            return response.json()
        }

        throw new Error()
        })
        .catch(function(error){
              console.log("Request failure: ", error);
        });
}

function playerInfo(data) {
var player;
var opponent;
var gamePlayerId = data.id;
var playerInfo = document.getElementById("playerInfo");


for (i = 0; i < data.gamePlayers.length; i++) {
    if (data.gamePlayers[i].id === gamePlayerId) {
    player = data.gamePlayers[i].player.username
        }
    else {
    opponent = data.gamePlayers[i].player.username
        }
    }
    playerInfo.innerHTML = 'This a game between you: <strong>' + player + '</strong> and your opponent <strong>' +
    opponent + '.</strong> Good luck!';
}


function createShipTable() {
        var rowLetters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
        var cellId;
        var cellNumber;
        var rowLetter;

            var createGrid=function(x,y){
                	var arrY = new Array(),
                            container = document.getElementById("shipTable");
                	for(var iy = 0; iy < y; iy++){
                		arrX = new Array();
                		for(var ix = 0; ix < x; ix++){
                			arrX[ix]=`<div class='cell shipCell' id = ${rowLetters[iy]}${ix +1}>${rowLetters[iy]}${ix
                			+1}</div>`;
                		}
                		arrY[iy]=`<div class="row" id=${rowLetters[iy]}>${arrX.join("\r\n")} </div>`;

                	}
                	container.innerHTML = arrY.join("\r\n");
                };
                createGrid(10,10);
    var shipCells = document.querySelectorAll("div.shipCell")
    shipCells[4].style.backgroundColor="red";
 }

function createSalvoTable() {
        var rowLetters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
        var cellId;
        var cellNumber;
        var rowLetter;

            var createGrid=function(x,y){
                	var arrY = new Array(),
                            container = document.getElementById("salvoTable");
                	for(var iy = 0; iy < y; iy++){
                		arrX = new Array();
                		for(var ix = 0; ix < x; ix++){
                			arrX[ix]=`<div class='cell salvoCell' id = ${rowLetters[iy]}${ix +1}>${rowLetters[iy]}${ix
                			+1}</div>`;
                		}
                		arrY[iy]=`<div class="row" id=${rowLetters[iy]}>${arrX.join("\r\n")} </div>`;

                	}
                	container.innerHTML = arrY.join("\r\n");
                };
                createGrid(10,10);
}

function getGamePlayerId() {

    var url_string = window.location.href
    var url = new URL(url_string);
    var gp = url.searchParams.get("gp");

    return gp;
}



function displayShips(ships) {
    var table = document.getElementById("shipTable");
    var cells = document.getElementsByClassName("shipCell");
    var shipLocations = [];

    for (var i = 0; i < ships.length; i++) {
        for (var j = 0; j < ships[i].locations.length; j++) {
            shipLocations.push(ships[i].locations[j])
        }
    }
    for (var k = 0; k < shipLocations.length; k++) {
        for (m = 0; m < cells.length; m++) {
        if (shipLocations[k] === cells[m].id) {
            cells[m].style.backgroundColor = "blue";}
        }
    }
}

function displayHits(data) {
    var shipCells = document.getElementsByClassName("shipCell");
    var salvoCells = document.getElementsByClassName("salvoCell");
    var gamePlayerId = data.id;

    var enemyHits = [];
    var playerHits = [];

    for (var n = 0; n < data.gamePlayers.length; n++) {
        if (data.gamePlayers[n].id == gamePlayerId) {
        playerSalvos = data.gamePlayers[n].salvos
        }
        if (data.gamePlayers[n].id !== gamePlayerId) {
        opponentSalvos = data.gamePlayers[n].salvos
        }

    }

    for (var i = 0; i < opponentSalvos.length; i++) {

        for (var j = 0; j < opponentSalvos[i].locations.length; j++) {
            enemyHits.push(opponentSalvos[i].locations[j])
        }
    }

    for (var i = 0; i < playerSalvos.length; i++) {
        for (var j = 0; j < playerSalvos[i].locations.length; j++) {
                playerHits.push(playerSalvos[i].locations[j])
         }
    }



    for (var k = 0; k < enemyHits.length; k++) {
            for (m = 0; m < shipCells.length; m++) {
            if (enemyHits[k] === shipCells[m].id) {
                shipCells[m].style.backgroundColor = "red";}
            }
        }

    for (var k = 0; k < playerHits.length; k++) {
            for (m = 0; m < salvoCells.length; m++) {
            if (playerHits[k] === salvoCells[m].id) {
                salvoCells[m].style.backgroundColor = "red";}
            }
        }

}





function loadShips() {
const carrier = {
    "name" : "carrier",
    "length" : 5
}
const destroyer = {
    "name" : "destroyer",
    "length" : 4
}
const frigate = {
    "name" : "frigate",
    "length" : 3
}
const cruiser = {
    "name" : "cruiser",
    "length" : 3
}
const submarine = {
    "name" : "submarine",
    "length" : 2
}

const ships = [submarine, carrier, destroyer, frigate, cruiser]
console.log(ships)

for (i = 0; i < ships.length; i++) {
    var shipsToBePlaced = document.getElementById("shipsToBePlaced");
    var shipRow = document.createElement("div");

    var shipLength = ships[i].length;

    for (j = 0; j < shipLength; j++){
    var shipCell = document.createElement("div");
    shipCell.setAttribute("class", "placementCell")

    shipRow.appendChild(shipCell);

        }
shipRow.setAttribute("id", ships[i].name)
shipRow.setAttribute("class", "row");
shipRow.setAttribute("draggable", "true");
shipRow.setAttribute("ondragstart", "drag(event)");
shipsToBePlaced.appendChild(shipRow);

    }
}

function allowDrop(ev) {
  ev.preventDefault();
}

function drag(ev) {
  ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
  ev.preventDefault();

  var data = ev.dataTransfer.getData("text");
//  var targetId = ev.target.id.split("")
//  var targetNumber = targetId[1];
  var shipTable = document.getElementById("shipTable");
  console.log(shipTable);

  if (data == "submarine") {
    console.log(ev.target);


//    for (i = 0, div; div = shipTable.divs[i]; i++) {
//    console.log("hi")
//    }


  }

  ev.target.appendChild(document.getElementById(data));
}