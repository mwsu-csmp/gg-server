let playerSprite;
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

    //getting images TODO: search for pixi texture loading
    playerSprite = document.getElementById("PlayerSprite");
    grassTile = document.getElementById("grass");
    deadTile = document.getElementById("grassDead");
    waterTile = document.getElementById("water");
    setupPixi();

    document.onkeydown = updateKeys;//gets key presses
    connect();
    //calls the method draw continuously every 300 ms
    //drawCharMap('outside');  // TODO: get board name from entity creation event for player avatar
    asdf = new Map();
    asdf.set("-", "grass1");
    asdf.set("#", "deadGrass");
    asdf.set("@", "door");
    asdf.set("*", "door");



    tileAlias = new Map();
    tileAlias.set("grass1",grassTile);
    tileAlias.set("deadGrass",deadTile);
    tileAlias.set("door",grassTile );

    boardHeight = 21//parsedJson.height;
    boardWidth = 17//parsedJson.width;
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



    createScene();
    setInterval(draw, 300);
}
function connect() {
    var socket = new SockJS('/WebSocketConfig');//connection link
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // Calls the method playerpos from PlayerController.java and
        // displays the return statement under canvas


        //TODO: example of somthing to do with ajax
        //sendInitRequest();


        stompClient.subscribe('/topic/event', function (event) {
           //
            //
            // console.log("pass in check: "+event.body.toString());
            eventReaction(event.body.toString());
        });
    });
}

// updates currentKey with the latest key pressed.
function updateKeys(e){

    let currentKey = e.key;
    console.log(currentKey);
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
            sendCommand("INTERACTION", "E")
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
    updatePlayer(container);
    boundaries();
    createScene();



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
    console.log(boardMap.length);

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

    updatePlayer(container);
}

function updatePlayer(appContainer){
    const pixiPSprite = PIXI.Texture.from(playerSprite);
    const userP = new PIXI.Sprite(pixiPSprite);
    userP.height = 60;
    userP.width = 60;
    userP.x = x;
    userP.y = y;
    appContainer.addChild(userP);
}



function getCharTile(tileType){
    return grassTile;
    // TODO: load known textures in to a map, access map here
    switch(tileType){
        case 'floor': return grassTile;
        default: return deadTile;
    }
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


function eventReaction(event) {
    //console.log("EVENTLOGGER:"+event);
    if(event.toString().includes("MOVE")){
        changeLocation(event);
    }
    else if(event.toString().includes("INTERACTION")){

    }
    else{
        console.log("Server gave me information that I cannot use.")
    }

}

function changeLocation(event) {
    //TODO: this is temp it will need to change to impliment row and collom not directly adding 40 to x and y
    if(event.toString().includes("NORTH")){
        y-=TILE_SIZE;
    }
    else if(event.toString().includes("WEST")){
        x-=TILE_SIZE;
    }
    else if(event.toString().includes("EAST")){
        x+=TILE_SIZE;
    }
    else{
        y+=TILE_SIZE;
    }

}

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