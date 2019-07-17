let playerSprite;
let grassTile;
let deadTile;

CANV_HEIGHT = 300;
CANV_WIDTH = 300;
SPR_HEIGHT = 40;
SPR_WIDTH = 40;
MOVE_VALUE = 60;
let histX;
let histY;
let x = 120;
let y = 120;
let dx = 0;
let dy = 0;
let currentKey;
let app;
let container;
let tileA2;
let drawing;
let con;

let boardInfoURL = '/board';

function init(){ // called on startup
    drawing = document.getElementById("cropTest");
    con = drawing.getContext("2d");
    //getting images TODO: search for pixi texture loading
    playerSprite = document.getElementById("PlayerSprite");
    grassTile = document.getElementById("grass");
    deadTile = document.getElementById("grassDead");
    setupPixi();

    document.onkeydown = updateKeys;//gets key presses
    connect();
    //calls the method draw continuously every 300 ms
    drawCharMap('outside');  // TODO: get board name from entity creation event for player avatar
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


        stompClient.subscribe('/topic/event', function (eventhelper) {
            eventPrint(JSON.parse(eventhelper.body).content)

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
    }

} // end updateKeys
function draw(){

    //move the image
    x += dx;
    dx = 0;
    y += dy;
    dy = 0;

    //check for boundaries
    boundaries();
    createScene();
    updatePlayer(container);


    //updating displayed coordinates
    document.getElementById('coordinates').innerHTML = "Coordinates: (" + (x/MOVE_VALUE) + "," + (y/MOVE_VALUE) + ")";
    document.getElementById('actualXY').innerHTML = "Coordinates: (" + x + "," + y + ")";

} // end of draw

//checks to see if the player is against a wall
function boundaries(){
    if (x >= 240){
        //subtracts offset of character sprite
        x = 240;
    }
    if (x < 0){
        x = 0;
    }
    if (y >= 240){
        //subtracts offset of character sprite
        y = 240;
    } // end if
    if (y < 0){
        y = 0;
    }
}//end of wrap

function setupPixi() {
    app = new PIXI.Application({
        width: CANV_WIDTH, height: CANV_HEIGHT,
        backgroundColor: 0x9999bb
        //resolution: window.devicePixelRatio || 1
    });

    document.body.appendChild(app.view);

    container = new PIXI.Container();
    app.stage.addChild(container);

//creating grass textures

}
function createScene(){
    const textureGrass = PIXI.Texture.from(grassTile);

    for(let i = 0; i < 70; i++){
        const grass = new PIXI.Sprite(textureGrass);

        grass.height = 60;
        grass.width = 60;
        grass.x = (i%10) * 60;
        grass.y = Math.floor(i/10) * 60;
        container.addChild(grass);

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




function drawCharMap(boardName){
    // TODO: this doesn't properly draw arbitrary maps yet. Not sure what the problem is.
    $.getJSON(boardInfoURL+'/'+boardName, function(board) { // pull board info from AJAX
        let xi;
        let yi = 0;
        var map = board.tilemap;  // retrieve map string from JSON
        var tileCharMap = board.tileTypes;
        for(xi = 0;xi<=map.length;xi++){

            if(map.charAt(xi) === "\n"){
                yi++;
                console.log(xi + "," + yi);
            }else{
                con.drawImage(getCharTile(tileCharMap[map.charAt(xi)]), (xi*60)-(yi*360), yi*60, 60,60);
                console.log("Drawing image at: " + xi + "," + yi);
            }
        }

    });
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


function eventPrint(event) {
    console.log("EVENTLOGGER:"+event);
}