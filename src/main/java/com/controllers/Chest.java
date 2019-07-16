package com.controllers;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.base.events.CommandEvent;

import java.util.Map;

public class Chest extends Entity implements Container {

//    protected TestEntity(Game game, Map<String, String> properties) {
//        super(game, properties);
//    }
    public Chest(Game game) {
        super(game, Map.of("sprites", "chest-normal",
                "character", "▣",
                "impassable", "true",
                "description", "a large chest"));
    }

//    This code should go as follows
//    if chest is empty{speech event chest is empty}
//    else {addEntity to player avatar & removeEntity from chest then speech event "got 'item'"
    public void accept(Event event) {
        var command = (CommandEvent)event;
        if(command.getCommandName().equals("INTERACT")) {
            if (this.isEmpty()){}
            else{

            }
        }
    }

    @Override
    public String getType() {
        return "chest";
    }
}
