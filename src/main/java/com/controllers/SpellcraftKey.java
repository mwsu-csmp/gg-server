package com.controllers;

import edu.missouriwestern.csmp.gg.base.Entity;
import edu.missouriwestern.csmp.gg.base.Game;

import java.util.Map;

public class SpellcraftKey extends Entity {

    public SpellcraftKey(Game game) {
        super(game, Map.of("sprites", "key-spellcraft",
                "character", "⚷",
                "impassable", "false",
                "description", "Key to Spellcraft foyer"));
    }

    @Override
    public String getType() {
        return "SpellcraftKey";
    }
}
