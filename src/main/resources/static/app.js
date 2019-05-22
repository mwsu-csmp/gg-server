var stompClient = null;
var drawing;
var con;
var playerSprite;
CANV_HEIGHT = 200;
CANV_WIDTH = 200;
SPR_HEIGHT = 40;
SPR_WIDTH = 40;
var x = 200;
var y = 200;

var currentKey;

function init(){ // called on startup (not using onLoad)
    drawing = document.getElementById("drawing");
    con = drawing.getContext("2d");
    playerSprite = document.getElementById("PlayerSprite");
    document.onkeydown = updateKeys;
   // connect();
    //calls the method draw every 100ms
    setInterval(draw, 1000);
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (updateKeys) {
            //showGreeting(JSON.parse(greeting.body).content);
           parseDraw(JSON.parse(greeting.body).content);
        });
    });
}

//updates currentKey with the latest key pressed.
function updateKeys(e){
    currentKey = e.key;
    switch (currentKey){

        case "a":
        case "A":
        case "ArrowLeft":
            actionJson(1,0);
            currentKey = null;
            break;

        case "d":
        case "D":
        case "ArrowRight":
            actionJson(-1,0);
            currentKey = null;
            break;

        case "w":
        case "W":
        case "ArrowUp":
            actionJson(0,-1);
            currentKey = null;
            break;

        case "s":
        case "S":
        case "ArrowDown":
            actionJson(0,1);
            currentKey = null;
            break;
    }


} // end updateKeys

//sends and receives JSON string
function actionJson(moveX, moveY){
    var move_to = moveX + "," + moveY;
    console.log(move_to);
        stompClient.send("/app/hello", {}, JSON.stringify({'move_to': $("#move_to").val()}));

}
function parseDraw(line){

}
function draw(dx, dy){
    //clear background
    con.clearRect(0, 0, 200, 200);
    //move the image
    if(dx == 'undefined'){ dx = 0}
    if(dy == 'undefined'){ dy = 0}
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
    document.getElementById('coordinates').innerHTML = "Coordinates: (" + (x/40) + "," + (y/40) + ")";
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