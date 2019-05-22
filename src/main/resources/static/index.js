
var drawing;
var con;
var playerSprite;
CANV_HEIGHT = 200;
CANV_WIDTH = 200;
SPR_HEIGHT = 40;
SPR_WIDTH = 40;
MOVE_VALUE = 40;
var x = 80;
var y = 80;
var dx = 0;
var dy = 0;

var currentKey;

function init(){ // called on startup (not using onLoad)
    drawing = document.getElementById("drawing");
    con = drawing.getContext("2d");
    playerSprite = document.getElementById("PlayerSprite");
    document.onkeydown = updateKeys;
    //calls the method draw every 100ms
    setInterval(draw, 10);
}

//updates currentKey with the latest key pressed.
function updateKeys(e){
    currentKey = e.key;
    switch (currentKey){

        case "a":
        case "A":
        case "ArrowLeft":
            dx = -MOVE_VALUE;
            currentKey = null;
            break;

        case "d":
        case "D":
        case "ArrowRight":
            dx = MOVE_VALUE;
            currentKey = null;
            break;

        case "w":
        case "W":
        case "ArrowUp":
            dy = -MOVE_VALUE;
            currentKey = null;
            break;

        case "s":
        case "S":
        case "ArrowDown":
            dy = MOVE_VALUE;
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