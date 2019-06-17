var drawing;
var con;
var playerSprite;
var stompClient = null;
CANV_HEIGHT = 200;
CANV_WIDTH = 200;
SPR_HEIGHT = 40;
SPR_WIDTH = 40;
MOVE_VALUE = 40;
var x = 80;
var y = 80;
var dx = 0;
var dy = 0;




function init(){ // called on startup
    console.log("v1");
    drawing = document.getElementById("drawing");
    con = drawing.getContext("2d");
    playerSprite = document.getElementById("PlayerSprite");
    document.onkeydown = updateKeys;//gets key presses
    connect();
    //calls the method draw continuously every 300 ms
    setInterval(draw, 300);
}
function connect() {
    var socket = new SockJS('/WebSocketConfig');//connection link
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
    });
}

// updates currentKey with the latest key pressed.
// SENDKEYPRESSED WILL BE UPDATED TO NOT BE HARD CODED
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

    //clear background
    con.clearRect(0, 0, 200, 200);
    //move the image
    x += dx;
    dx = 0;
    y += dy;
    dy = 0;

    //check for boundaries
    boundaries();


    //draw the image
    con.drawImage(playerSprite, x, y, SPR_WIDTH, SPR_HEIGHT);
    //draw a rectangle
    con.strokeStyle = "black";//border color
    con.lineWidth = 5;//line width
    con.strokeRect(0, 0, CANV_WIDTH, CANV_HEIGHT);

    //updating displayed coordinates
    document.getElementById('coordinates').innerHTML = "Coordinates: (" + (x/MOVE_VALUE) + "," + (y/MOVE_VALUE) + ")";
    document.getElementById('actualXY').innerHTML = "Coordinates: (" + x + "," + y + ")";

} // end of draw

//checks to see if the player is against a wall
function boundaries(){
    if (x >= CANV_WIDTH){
        //subtracts offset of character sprite
        x = CANV_WIDTH - SPR_WIDTH;
    }
    if (x < 0){
        x = 0;
    }
    if (y >= CANV_HEIGHT){
        //subtracts offset of character sprite
        y = CANV_HEIGHT - SPR_HEIGHT;
    } // end if
    if (y < 0){
        y = 0;
    }
}//end of wrap

//sends coordinates (currently only x) to server
function sendDX() {
    stompClient.send("/index/com/gg/player", {}, JSON.stringify(x));
}

//sends last key pressed to server
function sendKeyPressed(e){
    stompClient.send("/index/com/gg/keyboardinput", {}, JSON.stringify(e));
}

// ***** The following methods display 'debugging'    *****
// ***** information that's retrieved from the server *****

//x (soon to be y) coordinates
function showXY(coordinates) {
     document.getElementById('serverX').innerHTML = "Server Coordinates: " + coordinates;
}

//last key pressed (not working yet)
function showKeyPressed(keyPressed){
    document.getElementById('keyPressed').innerHTML = "Last Key Pressed: " + keyPressed;
}