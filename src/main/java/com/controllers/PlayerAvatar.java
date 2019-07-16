package com.controllers;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.base.events.CommandEvent;
import edu.missouriwestern.csmp.gg.base.events.GameStartEvent;

import java.util.HashMap;
import java.util.Map;

/** gives a player a presence within the game */
public class PlayerAvatar extends Entity implements EventListener {

    private final Player player;

    public PlayerAvatar(Game game, Player player) {
        super(game, new HashMap<>());
        this.player = player;
    }

    /** returns the player associated with this avatar entity */
    public Player getPlayer() { return player; }

    @Override
    public void accept(Event event) {
        if(event instanceof CommandEvent) {
            if(event.getProperty("player").equals(player.getID())) {
                switch(event.getProperty("command")) {
                    case "MOVE": // move entity in the specified direction
                        var location = getGame().getEntityLocation(this);
                        if(!(location instanceof Tile)) return;
                        var tile = (Tile)location;
                        var board = tile.getBoard();
                        var direction = Direction.valueOf(event.getProperty("parameter"));
                        var destination = board.getAdjacentTile(tile, direction);
                        if(destination != null) { // if we're walking to a valid location...
                            if(destination.hasProperty("impassable") &&
                                    !destination.getProperty("impassable").equals("false"))
                                break;  // can't walk on to an impassable tile

                            // determine if we walked through a door
                            if(destination.hasProperty("portal-destination-board")) {
                                destination = getGame()  // update destination to new board
                                        .getBoard(destination.getProperty("portal-destination-board"))
                                        .getTileStream()
                                        .filter(t -> t.hasProperty("entering-entity-spawn"))
                                        .findFirst().get();
                            }

                            getGame().moveEntity(this, destination);
                        }
                        break;

                }
            }
        }
    }

    public String getType() {
        return "player-avatar";
    }
}