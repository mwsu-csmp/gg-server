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

let currentBoardName;
let boardWidth;
let boardHeight;
let charAlias = new Map();
let tileAlias;
let boardMap;
let asdf;
let entitySprites = {};

TILE_SIZE = 60;
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


    app = new PIXI.Application({
        width: (20) * TILE_SIZE, height: 20 * TILE_SIZE,
        backgroundColor: 0x9999bb
    });

    document.body.appendChild(app.view);

    container = new PIXI.Container();
    app.stage.addChild(container);

    // retrieve username
    username= $($.find('h1')[0]).html();


    // connect to STOMP
    var socket = new SockJS('/WebSocketConfig');//connection link
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/event', function (message) {
            eventReaction(JSON.parse(message.body));
        });

        // determine player avatar and draw board
        $.getJSON("../player-avatar/"+username,function (entity) {
            myUserEnityId= entity.id;
            loadBoard(entity.board);
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

function loadBoard(boardName){
    $.getJSON(boardInfoURL+'/'+boardName, function(board){
        // first clear the board
        container.removeChildren();
        entitySprites = {};

        // load board details
        currentBoardName = boardName;
        boardWidth = board.width+1;
        boardHeight = board.height;
        boardMap = board.tilemap;
        console.log(board);

        // create board tiles
        let pos = 0;
        for(let iy = 0; iy < boardHeight; iy++){
            for(let ix = 0; ix < boardWidth; ix++) {
                switch(boardMap.charAt(pos)){
                    case "\n":
                        pos++;
                        break;
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
    });
}

function drawEntity(entity){
    if(entitySprites[entity.id]) { // entity has a sprite
        sprite = entitySprites[entity.id];
        container.removeChild(sprite);
        sprite.x = entity.column * TILE_SIZE;
        sprite.y = entity.row * TILE_SIZE;
        container.addChild(sprite);
    } else { // create sprite for entity
        // TODO: determine texture from entity properties
        entityImage = playerSprite;
        const texture = PIXI.Texture.from(entityImage);
        const sprite = PIXI.Sprite.from(texture);
        sprite.x = entity.column * TILE_SIZE;
        sprite.y = entity.row * TILE_SIZE;
        sprite.height = TILE_SIZE;
        sprite.width = TILE_SIZE;
        container.addChild(sprite);
        entitySprites[entity.id] = sprite;
    }
}


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

        case "EntityCreatedEvent":
        case "EntityMovedEvent":
            $.getJSON("../entity/"+event.properties.entity,function (entity) {

                // check to see if it is the current player's avatar
                if (entity.properties.player!=undefined){
                    enityUserName=entity.properties.player;
                    if (enityUserName==username){
                        if(entity.board != currentBoardName) {
                            loadBoard(entity.board);
                        }
                    }
                }

                if(entity.board = currentBoardName) { // draw it if it's on our board
                    drawEntity(entity);
                }
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
