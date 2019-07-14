package com.player;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.base.Player;

public class StompClient extends Player {

    public StompClient(String id, Game game) {
        super(id, game);
    }

    @Override
    public String getID() {
        return super.getID();
    }

    // listen for events to communicate back to client
    public void accept(Event event) {

    }
}
