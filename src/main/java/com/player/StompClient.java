package com.player;

import com.controllers.PlayerAvatar;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.base.Player;

public class StompClient extends Player {

    private final PlayerAvatar avatar;

    public StompClient(String id, Game game) {
        super(id, game);
        avatar = new PlayerAvatar(game, this);
    }

    @Override
    public String getID() {
        return super.getID();
    }

    // listen for events to communicate back to client
    public void accept(Event event) {

    }
}
