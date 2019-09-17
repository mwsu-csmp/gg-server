package edu.missouriwestern.csmp.gg.server.controllers;
import edu.missouriwestern.csmp.gg.base.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.security.Principal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Controller;

import static edu.missouriwestern.csmp.gg.base.events.CommandEvent.issueCommandEventFromJson;

@Controller
public class PlayerController {

    private Gson gson;

    @Autowired
    private Game game;

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
        issueCommandEventFromJson(game, user.getName(), data);


    }

}