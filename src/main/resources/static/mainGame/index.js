let playerSprite;
let GuideSprite;
let OtherPlayerSprite;
let grassTile;
let deadTile;
let waterTile;

CANV_HEIGHT = 300;
CANV_WIDTH = 300;

let boardWidth;
let boardHeight;
let charAlias = new Map();
let tileAlias;
let boardMap;
let asdf;

let column;
let row;

TILE_SIZE = 60;
let x = 120;
let y = 120;
let dx = 0;
let dy = 0;
let currentKey;
let app;
let container;

let boardInfoURL = '/board';

function init(){ // called on startup

    let username = $($.find('#username')[0]).text();
    //getting images TODO: search for pixi texture loading
    playerSprite = document.getElementById("PlayerSprite");
    OtherPlayerSprite = document.getElementById("OtherPlayerSprite");
    GuideSprite = document.getElementById("GuideSprite");

    grassTile = document.getElementById("grass");
    deadTile = document.getElementById("grassDead");
    waterTile = document.getElementById("water");


    document.onkeydown = updateKeys;//gets key presses
    connect();
      // TODO: get board name from entity creation event for player avatar
    asdf = new Map();
    asdf.set("-", "grass1");
    asdf.set("#", "deadGrass");
    asdf.set("@", "door");
    asdf.set("*", "door");



    tileAlias = new Map();
    tileAlias.set("grass1",grassTile);
    tileAlias.set("deadGrass",deadTile);
    tileAlias.set("door",waterTile );

    boardHeight = 21;//parsedJson.height;
    boardWidth = 17;//parsedJson.width;
    charAlias = asdf;
    boardMap =
        "########@########" +
        "########-########" +
        "########-########" +
        "-##----------###-" +
        "-----------------" +
        "-----------------" +
        "-----------------" +
        "-----------------" +
        "#---------------#" +
        "#---------------#" +
        "#---------------#" +
        "#---------------#" +
        "#---------------#" +
        "#---------------#" +
        "#---------------#" +
        "#---------------#" +
        "#---------------#" +
        "#---------------#" +
        "#---------------#" +
        "#-------*-------#" +
        "#################";


    setupPixi();
    createScene();
    setInterval(draw, 300);
}
function connect() {
    var socket = new SockJS('/WebSocketConfig');//connection link
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);


        //TODO: example of somthing to do with ajax



        stompClient.subscribe('/topic/event', function (message) {
           //
            //
            // console.log("pass in check: "+event.body.toString());

          //  console.log("yeeeeeeeeeeeet"+username);

            let pmessage = JSON.parse(message.body);
            //this would log the body in case you need to see it
            //console.log(pmessage);
            eventReaction(pmessage);
        });
    });
}

// updates currentKey with the latest key pressed.
function updateKeys(e){

    let currentKey = e.key;
    switch (currentKey){

        case "a":
        case "A":
        case "ArrowLeft":
            sendCommand("MOVE", "WEST");
            currentKey = null;
            break;

        case "d":
        case "D":
        case "ArrowRight":
            sendCommand("MOVE", "EAST");
            currentKey = null;
            break;

        case "w":
        case "W":
        case "ArrowUp":
            sendCommand("MOVE", "NORTH");
            currentKey = null;
            break;

        case "s":
        case "S":
        case "ArrowDown":
            sendCommand("MOVE", "SOUTH");
            currentKey = null;
            break;

        case "e":
        case "E":
            sendCommand("INTERACTION", "E");
            currentKey=null;
            break;
    }

} // end updateKeys

function draw(){

    //move the image
    x += dx;
    dx = 0;
    y += dy;
    dy = 0;

    //check for boundaries
    drawEntity(playerSprite, x, y);
    boundaries();
    //createScene();



    //updating displayed coordinates
    document.getElementById('coordinates').innerHTML = "Coordinates: (" + (x/TILE_SIZE) + "," + (y/TILE_SIZE) + ")";
    document.getElementById('actualXY').innerHTML = "Coordinates: (" + x + "," + y + ")";

} // end of draw

//checks to see if the player is against a wall
function boundaries(){
    if (x >= (boardWidth * TILE_SIZE)){
        //subtracts offset of character sprite
        x = (boardWidth * TILE_SIZE)-TILE_SIZE;
    }
    if (x < 0){
        x = 0;
    }
    if (y >= (boardHeight*TILE_SIZE)){
        //subtracts offset of character sprite
        y = (boardHeight*TILE_SIZE)-TILE_SIZE;
    } // end if
    if (y < 0){
        y = 0;
    }
}//end of wrap

function setupPixi() {
    app = new PIXI.Application({
        width: 1020, height: 1260,
        backgroundColor: 0x9999bb
        //resolution: window.devicePixelRatio || 1
    });

    document.body.appendChild(app.view);

    container = new PIXI.Container();
    app.stage.addChild(container);

//creating grass textures

}
function createScene(){
    let pos = 0;

    for(let iy = 0; iy < boardHeight; iy++){
        for(let ix = 0; ix < boardWidth; ix++) {
            const textureGrass = PIXI.Texture.from(getTile(boardMap.charAt(pos)));

            const tile = new PIXI.Sprite(textureGrass);
            tile.height = TILE_SIZE;
            tile.width = TILE_SIZE;
            tile.x = ix * TILE_SIZE;
            tile.y = iy * TILE_SIZE;
            container.addChild(tile);
            pos++;
        }
    }

    drawEntity(playerSprite, x, y);
}

function drawEntity(entitySprite, xCoord, yCoord){

    const pixiPSprite = PIXI.Texture.from(entitySprite);
    const entityTile = PIXI.Sprite.from(pixiPSprite);
    entityTile.height = 60;
    entityTile.width = 60;
    entityTile.x = xCoord;
    entityTile.y = yCoord;
    container.addChild(entityTile);
}


// ***** The following methods display 'debugging'    *****
// ***** information that's retrieved from the server *****


//sends coordinates (currently only x) to server
function sendDX(){
    stompClient.send("/index/com/gg/player", {}, JSON.stringify({x: this.x, y: this.y}));
}

//sends a command
function sendCommand(command, parameter) {
    stompClient.send("/index/gg/command", {}, JSON.stringify(
        {
            "command": command,
            "parameter": parameter
        }
    ));
}




function sendInitRequest(){
    console.log("Board Received");
    stompClient.send("/index/com/gg/board", {}, JSON.stringify(1))

}

function showXY(coordinates) {
    document.getElementById('serverXY').innerHTML = "Server Coordinates: " + coordinates;
}

//last key pressed (not working yet)
function showKeyPressed(keyPressed){
    document.getElementById('keyPressed').innerHTML = "Last Key Pressed: " + keyPressed;
}



function showTiles(tiletext) {
    document.getElementById('tileLocation').innerHTML = "Tile: " + tiletext;
    console.log(tiletext);
}


function eventReaction(message) {


    //a command event is a player command
    if(message.type.includes("CommandEvent")){
        //TODO: need to add an ability to distiguish if its for the main player or a different player
        //TODO: the if below is for the ability to know if it was thier own player or not
        if(true){




        }
        //this is for non main player events
        else{




        }



    }
    //this is essentally used by guide. non player movments have occured.
    else if(message.type.includes("EntityMovedEvent")){




    }
    //if it was not any thing listed above then the client is unsure what to do with the broadcasted information
    else{
        console.log("~~~~~Unusable information~~~~~")
    }

}





//returns the tile from a map of tiles using the value of another map
function getTile(character){
    switch(character){
        case "-":
            return tileAlias.get(charAlias.get("-"));
        case "#":
            return tileAlias.get(charAlias.get("#"));
        case "@":
            return tileAlias.get(charAlias.get("@"));
        case "*":
            return tileAlias.get(charAlias.get("*"));

    }
}


//TODO: look into coloring console.log information
var styles = [
    'background: linear-gradient(#D33106, #571402)'
    , 'border: 1px solid #3E0E02'
    , 'color: white'
    , 'display: block'
    , 'text-shadow: 0 1px 0 rgba(0, 0, 0, 0.3)'
    , 'box-shadow: 0 1px 0 rgba(255, 255, 255, 0.4) inset, 0 5px 3px -5px rgba(0, 0, 0, 0.5), 0 -13px 5px -10px rgba(255, 255, 255, 0.4) inset'
    , 'line-height: 40px'
    , 'text-align: center'
    , 'font-weight: bold'
].join(';');

