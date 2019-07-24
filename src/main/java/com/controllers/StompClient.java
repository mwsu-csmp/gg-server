package com.controllers;

import com.controllers.PlayerAvatar;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.base.Player;

public class StompClient extends Player {

    private final PlayerAvatar avatar;

    public StompClient(String id, Game game) {
        super(id, game);
        avatar = new PlayerAvatar(game, this);
        game.registerListener(avatar);
        // TODO: place player avatar where it needs to go
    }

    public PlayerAvatar getAvatar() { return avatar; }

    @Override
    public String getID() {
        return super.getID();
    }

    // listen for events to communicate back to client
    public void accept(Event event) {

    }
}
