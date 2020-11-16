package edu.missouriwestern.csmp.gg.server.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller("player-controller")
public class PlayerController {

    private Gson gson;

    @Autowired private Game game;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public PlayerController() {
        var gb = new GsonBuilder();
        gson = gb.create();
    }

    @MessageMapping("/gg/command")
    public void receiveCommand(
            String data,
            Principal user
    ){
        var parser = new JsonParser();
        var element = parser.parse(data);
        game.propagateEvent(new Event(game, "command", Map.of(
                "username", user.getName(),
                "command", element.getAsJsonObject().get("command").getAsString(),
                "parameter", element.getAsJsonObject().get("parameter").getAsString()
        )));
    }
}