let playerSprite;
let GuideSprite;
let OtherPlayerSprite;
let grassTile;
let deadTile;
let waterTile;
let missingTexture;


let playerUser;
let playerUserBoard;
let playerUserSpawn;
//TODO: store any entity that is given
let playerAdmin;
let playerGuide;

let lastMovement="";
let username;
let myUserEntityId;
let helperTemp;//for eventReactionHelper

let boardWidth;
let boardHeight;
let charAlias = new Map();
let tileAlias;
let boardMap;
let asdf;
let lastLocation = {x:0,y:0};//column/row not pixel

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
    //TODO: fix onstart loading error to avoid hard coding this.


      // TODO: get board name from entity creation event for player avatar
    asdf = new Map();
    asdf.set("-", "grass1");
    asdf.set("#", "deadGrass");
    asdf.set("@", "door");
    asdf.set("*", "door");
    asdf.set("%", "grass1");




    tileAlias = new Map();
    tileAlias.set("grass1",grassTile);
    tileAlias.set("deadGrass",deadTile);
    tileAlias.set("door",waterTile );
    tileAlias.set("guide", GuideSprite);

    charAlias = asdf;



    //ajax info pulling

    getInfo('outside');//gets board info
    setupPixi();
    createScene();

}
function connect() {
    var socket = new SockJS('/WebSocketConfig');//connection link
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/event', function (message) {
            getUser();
            eventReaction(JSON.parse(message.body));
        });
    });
}

//this is to get the name of the client from the htlm through ajax
function getUser(){
    username= $($.find('h1')[0]).html();
    console.log("Username: "+ username);
    getUsername(username);


}

function getUsername(username) {
    $.getJSON("../player-avatar/"+username,function (entity) {
        myUserEntityId= entity.id;
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
            lastMovement="SOUTH";
            break;

        case "e":
        case "E":
            sendCommand("INTERACT", lastMovement.toString());
            // I have to string above just to ensure that the send is all string. just in case.
            currentKey=null;
            break;
    }

} // end updateKeys



// ***** The following methods display 'debugging'    *****
// ***** information that's retrieved from the server *****
//sends a command
function sendCommand(command, parameter) {
    stompClient.send("/index/gg/command", {}, JSON.stringify(
        {
            "command": command,
            "parameter": parameter
        }
    ));
}

function eventReaction(event) {

    switch (event.type) {
        //TODO: need to add an ability to distiguish if its for the main player or a different player
        //the if below is for the ability to know if it was their own player or not
        case "EntityMovedEvent":

            $.getJSON("../entity/"+event.properties.entity,function (entity) {


                let entityUserName;
                console.log("\n\n~~~ ENTITY DESCRIPTION ~~~");
                console.log(entity);


                if (entity.properties.player!=undefined){
                    entityUserName=entity.properties.player;
                    if (entityUserName==username){
                        playerUser = {
                            sprite:playerSprite,xCo:entity.column*TILE_SIZE,yCo:entity.row*TILE_SIZE
                        };
                        console.log("%%%%%%%%%%%%%%%%%%%%%%%%%");
                        playerUserBoard = entity.board;


                        //updateScene();
                    }
                    else{
                        playerAdmin = {
                            sprite:OtherPlayerSprite,xCo:entity.column*TILE_SIZE,yCo:entity.row*TILE_SIZE
                        };
                        //updateScene();
                    }

                }
                else if(entity.properties.sprites=="guide"){
                    playerGuide = {
                        sprite:GuideSprite,xCo:entity.column*TILE_SIZE,yCo:entity.row*TILE_SIZE
                    };
                    //updateScene();
                }
                else{//dont know what they are
                    console.log("unknown entity movement");
                }

                updateScene();
            });

            break;
        case "SpeechEvent":
            window.alert(event.properties.message);
            break;
        case "CommandEvent":
            //ignore
            break;

        default:
            console.log("unregistered Event");

    }
}

function eventReactionHelper(entityNum) {
    $.getJSON("../entity/"+entityNum,function (entity) {
       helperTemp=entity;

    });
}

function setupPixi() {
    app = new PIXI.Application({
        width: (boardWidth-1) * TILE_SIZE, height: boardHeight * TILE_SIZE,
        backgroundColor: 0x9999bb
    });

    document.body.appendChild(app.view);

    container = new PIXI.Container();
    app.stage.addChild(container);

}

function createScene(){
    let pos = 0;
    for(let iy = 0; iy < boardHeight; iy++){
        for(let ix = 0; ix < boardWidth; ix++) {
            switch(boardMap.charAt(pos)){
                case "\n":
                    pos++;
                    break;
                case "*":
                    playerUserSpawn = {xCol:ix,yCol:iy};
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


}

function drawEntity(entitySprite, xCoord, yCoord){
    if(entitySprite === null ) {
    }else{
        const pixiPSprite = PIXI.Texture.from(entitySprite);
        const entityTile = PIXI.Sprite.from(pixiPSprite);
        entityTile.height = 60;
        entityTile.width = 60;
        entityTile.x = xCoord;
        entityTile.y = yCoord;
        container.addChild(entityTile);
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


        playerUser = {sprite:playerSprite,xCo:x,yCo:y};
        playerAdmin = {sprite:OtherPlayerSprite,xCo:x,yCo:y};
        playerGuide = {sprite:playerSprite,xCo:0,yCo:0};

    });


}

//TODO: make modular by using Entity ID # and iteration
function updateScene(){
    createScene();
    drawEntity(playerGuide.sprite,playerGuide.xCo,playerGuide.yCo);
    drawEntity(playerAdmin.sprite,playerAdmin.xCo,playerAdmin.yCo);
    drawEntity(playerUser.sprite,playerUser.xCo,playerUser.yCo);
}


function loadNewBoard(){
    container.destroy();
    app.destroy();
    $('canvas').remove();
    getInfo(playerUserBoard);
    setupPixi();
    createScene();
    updateScene();
}