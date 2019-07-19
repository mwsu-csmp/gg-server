package com.controllers;
import edu.missouriwestern.csmp.gg.base.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.security.Principal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Controller;

import org.springframework.beans.factory.annotation.Autowired;

import static edu.missouriwestern.csmp.gg.base.events.CommandEvent.issueCommandEventFromJson;

@Controller
public class PlayerController {

    private Gson gson;

    @Autowired
    private GameMapping game;

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