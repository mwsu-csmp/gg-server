package com.controllers;

import com.player.PlayerMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Controller
public class PlayerController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    // TODO: receive CommandEvents here instead and publish them for all event listeners
    @MessageMapping(value = "/com/player")
    @SendTo("/topic/moveto")
    public Message playerpos(PlayerMessage player){
        log.info("getting the player position");
        String cords = String.format("(%d, %d)", player.getX(), player.getY());
        return new Message(HtmlUtils.htmlEscape(cords));
    }

}