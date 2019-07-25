let playerSprite;
let GuideSprite;
let OtherPlayerSprite;
let grassTile;
let deadTile;
let waterTile;
let missingTexture;

let lastMovement="";
let username;
let myUserEnityId;
let helperTemp;//for eventReactionHelper
let helperTemp1;//for get row
let helperTemp2;//for get column

let boardWidth;
let boardHeight;
let charAlias = new Map();
let tileAlias;
let boardMap;
let asdf;

let column;
let row;

TILE_SIZE = 60;
let x;
let y;
let dx = 0;
let dy = 0;
let app;
let container;

let boardInfoURL = '/board';

function init(){ // called on startup

    //getting images TODO: search for pixi texture loading
    playerSprite = document.getElementById("PlayerSprite");
    OtherPlayerSprite = document.getElementById("OtherPlayerSprite");
    GuideSprite = document.getElementById("GuideSprite");
    missingTexture = document.getElementById("noTexture");
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
    asdf.set("%", "guide");




    tileAlias = new Map();
    tileAlias.set("grass1",grassTile);
    tileAlias.set("deadGrass",deadTile);
    tileAlias.set("door",waterTile );
    tileAlias.set("guide", GuideSprite);

    charAlias = asdf;




    getInfo('aggensteinFoyer');//gets board info
    setInterval(draw, 300);


}
function connect() {
    var socket = new SockJS('/WebSocketConfig');//connection link
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        //TODO: find a better place for whoAMI but this does garenttee we know who we are.
        whoAmI();
        stompClient.subscribe('/topic/event', function (message) {
            eventReaction(JSON.parse(message.body));
        });
    });
}

//this is to get the name of the client from the htlm through ajax
function whoAmI(){


    username= $($.find('h1')[0]).html();
    console.log("WHO AMMMM I: "+ username);

    //this is an attempt to try and get the client to find out its entity number
    //whoAmIHelper(3,username);
    //player-avatar/user

    whoAmIHelper(username);


}

function whoAmIHelper(userr) {
    $.getJSON("../player-avatar/"+userr,function (entity) {
        myUserEnityId= entity.id;
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
            lastMovement="WEST";
            break;

        case "d":
        case "D":
        case "ArrowRight":
            sendCommand("MOVE", "EAST");
            currentKey = null;
            lastMovement="EAST";
            break;

        case "w":
        case "W":
        case "ArrowUp":
            sendCommand("MOVE", "NORTH");
            currentKey = null;
            lastMovement="NORTH";
            break;

        case "s":
        case "S":
        case "ArrowDown":
            sendCommand("MOVE", "SOUTH");
            currentKey = null;
            lastMovement="South";
            break;

        case "e":
        case "E":
            sendCommand("INTERACTION", lastMovement.toString());
            // I have to string above just to ensure that the send is all string. just in case.
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
        width: (boardWidth-1) * TILE_SIZE, height: boardHeight * TILE_SIZE,
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
    console.log("at scene" + boardWidth + "," + boardHeight);
    for(let iy = 0; iy < boardHeight; iy++){
        for(let ix = 0; ix < boardWidth; ix++) {
            switch(boardMap.charAt(pos)){
                case "\n":
                    pos++;
                    break;
                case "*":
                    x = ix * TILE_SIZE;
                    y = iy * TILE_SIZE;
                default:
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
    }

    //drawEntity(playerSprite, x, y);
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


function eventReaction(event) {

    switch (event.type) {
        //TODO: need to add an ability to distiguish if its for the main player or a different player
        //TODO: the if below is for the ability to know if it was their own player or not
        case "EntityMovedEvent":
            console.log(event);
            let temp= eventReactionHelper(event.properties.entity);
            let tempCol;
            let tempRow;
            switch (temp) {
                case "player":
                    tempCol=getColumn(event.properties.entity);
                    tempRow=getRow(event.properties.entity);
                    if(event.properties.entity==myUserEnityId){
                        console.log("I moved- im at row:"+tempRow+"  column:"+tempCol);
                        createScene();
                        drawEntity(playerSprite,tempRow*TILE_SIZE,tempCol*TILE_SIZE);


                    }
                    else{
                        console.log("someone else moved- im at row:"+tempRow+"  column:"+tempCol);
                        createScene();
                        drawEntity(OtherPlayerSprite,tempRow*TILE_SIZE,tempCol*TILE_SIZE);
                    }

                    break;
                case "guide":
                    tempCol=getColumn(event.properties.entity);
                    tempRow=getRow(event.properties.entity);
                    console.log("the guide moved- im at row:"+tempRow+"  column:"+tempCol);
                    createScene();
                    drawEntity(GuideSprite,tempRow*TILE_SIZE,tempCol*TILE_SIZE);
                    break;
                default:
                    console.log("Client does not know this enity")
            }

            //drawEntity(missingTexture, event.column * TILE_SIZE, event.row * TILE_SIZE);
            break;
        default:
            console.log("unregistered Event");

    }
}

function eventReactionHelper(entityNum) {
    $.getJSON("../entity/"+entityNum,function (entity) {
        if(entity.properties.player!=undefined){
            helperTemp= "player";
        }
        else if(entity.properties.sprites=="guide"){
            helperTemp= "guide";
        }
        else{
            helperTemp= "unknown";
        }

    });
    return helperTemp;
}

function getRow(entityNum) {
    $.getJSON("../entity/"+entityNum,function (entity) {
        helperTemp1=entity.row;
    });
    return helperTemp1;
}

function getColumn(entityNum) {
    $.getJSON("../entity/"+3,function (entity) {
        helperTemp2=entity.column;
    });
    return helperTemp2;
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
        case "%":
            return tileAlias.get(charAlias.get("%"));

        default: return missingTexture;


    }
}

function getInfo(boardName){
    $.getJSON(boardInfoURL+'/'+boardName, function(board){

        boardWidth = board.width+1;
        boardHeight = board.height;
        boardMap = board.tilemap;
        console.log(boardWidth + "," + boardHeight);
        setupPixi();
        createScene();
    });


}




//TODO: look into coloring console.log information
/*
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

*/