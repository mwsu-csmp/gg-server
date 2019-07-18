package com.controllers;

import com.player.StompClient;
import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.base.events.CommandEvent;

import java.util.Map;

public class Chest extends Entity implements Container {

//    protected TestEntity(Game game, Map<String, String> properties) {
//        super(game, properties);
//    }
    public Chest(Game game, Container startingLocation) {
        super(game, Map.of("sprites", "chest-normal",
                "character", "▣",
                "impassable", "true",
                "description", "a large chest"),
                startingLocation);
    }


    public void accept(Event event) {
        if (event instanceof CommandEvent) { // see if someone wants you to talk to them
            var command = (CommandEvent) event;
            if (command.getCommandName().equals("INTERACT")) {
                var player = getGame().getPlayer(command.getProperty("player"));
                if (player instanceof StompClient) {
                    var avatar = ((StompClient) player).getAvatar();
                    var avatarLocation = getGame().getEntityLocation(avatar);
                    if(avatarLocation instanceof Tile) {
                        var tile = (Tile)avatarLocation;
                        var board = tile.getBoard();
                        var target = board.getAdjacentTile(tile, Direction.valueOf(command.getProperty("parameter")));
                        if(target == getGame().getEntityLocation(this)) {
                            getEntities().forEach(avatar::addEntity);
                        }
                    }
                }
            }
        }
    }


    @Override
    public String getType() {
        return "chest";
    }
}
