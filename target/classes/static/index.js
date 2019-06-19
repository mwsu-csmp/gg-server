let drawing;
let con;
let playerSprite;
let stompClient = null;
CANV_HEIGHT = 200;
CANV_WIDTH = 200;
SPR_HEIGHT = 40;
SPR_WIDTH = 40;
MOVE_VALUE = 40;
let x = 120;
let y = 120;
let dx = 0;
let dy = 0;

let app;
let container;
let grassTile;

function init(){ // called on startup
    console.log("v2");
    drawing = document.getElementById("drawing");
    con = drawing.getContext("2d");
    //getting images TODO: search for pixi texture loading
    playerSprite = document.getElementById("PlayerSprite");
    grassTile = document.getElementById("grass");
    setupPixi();

    document.onkeydown = updateKeys;//gets key presses
    connect();
    //calls the method draw continuously every 300 ms

    setInterval(draw, 300);
}
function connect() {
    let socket = new SockJS('/WebSocketConfig');//connection link
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // Calls the method playerpos from PlayerController.java and
        // displays the return statement under canvas
        stompClient.subscribe('/topic/moveto', function(playerpos) {
            showXY(JSON.parse(playerpos.body).content);
        });
        stompClient.subscribe('/topic/keyboard', function(readKey) {
            showKeyPressed(JSON.parse(readKey.body).content);
        });
        stompClient.subscribe('/topic/room', function(readKey) {
            drawTiles(JSON.parse(readKey.body).content);
        });
        sendInitRequest();

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
            dx = -MOVE_VALUE;
            sendKeyPressed(currentKey);
            sendDX();
            currentKey = null;
            break;

        case "d":
        case "D":
        case "ArrowRight":
            dx = MOVE_VALUE;
            sendKeyPressed(currentKey);
            sendDX();
            currentKey = null;
            break;

        case "w":
        case "W":
        case "ArrowUp":
            dy = -MOVE_VALUE;
            sendKeyPressed(currentKey);
            sendDX();
            currentKey = null;
            break;

        case "s":
        case "S":
        case "ArrowDown":
            dy = MOVE_VALUE;
            sendKeyPressed(currentKey);
            sendDX();
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
        width: 300, height: 300,
        backgroundColor: 0x9999bb
        //resolution: window.devicePixelRatio || 1
    });

    document.body.appendChild(app.view);

    container = new PIXI.Container();
    app.stage.addChild(container);

//creating grass textures

}
function createScene(){
    const textureGrass = PIXI.Texture.from('grass.png');
    let size = 40;
    for(let i = 0; i < 70; i++){
        const grass = new PIXI.Sprite(textureGrass);

        //grass.anchor.set(0);
        grass.height = size;
        grass.width = size;
        grass.x = (i%10) * size;
        grass.y = Math.floor(i/10) * size;
        container.addChild(grass);

    }
    updatePlayer(container);
}

function updatePlayer(appContainer){
    const pixiPSprite = PIXI.Texture.from(playerSprite);
    const userP = new PIXI.Sprite(pixiPSprite);
    userP.height = 50;
    userP.width = 30;
    userP.x = x+15;
    userP.y = y+10;
    //userP.anchor.set(0);
    appContainer.addChild(userP);
}


function drawTiles(tileCoord){
    let coord = tileCoord.split(",");
    console.log(coord.length);

    for(let i=0;i<coord.length-1;i+=2){
        con.drawImage(grassTile,coord[i],coord[i+1])
    }

}

// ***** The following methods display 'debugging'    *****
// ***** information that's retrieved from the server *****


//sends coordinates (currently only x) to server
function sendDX(){
    stompClient.send("/index/com/gg/player", {}, JSON.stringify({x: this.x, y: this.y}));
}

//sends last key pressed to server
function sendKeyPressed(e){
    stompClient.send("/index/com/gg/keyboardinput", {}, JSON.stringify(e));
}

function sendInitRequest(){
    console.log("Board Received");
    stompClient.send("/index/com/gg/board", {}, JSON.stringify(1))

}

//x (soon to be y) coordinates
function showXY(coordinates) {
     document.getElementById('serverXY').innerHTML = "Server Coordinates: " + coordinates;
}

//last key pressed (not working yet)
function showKeyPressed(keyPressed){
    document.getElementById('keyPressed').innerHTML = "Last Key Pressed: " + keyPressed;
}
