package com.controllers;

import com.rooms.Sandbox;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Controller
public class RoomController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @MessageMapping(value = "/com/board")
    @SendTo("/topic/room")
    public Message createRoom(Sandbox room) throws Exception{

        log.info("sending tile locations to client");
        return new Message(HtmlUtils.htmlEscape(room.getRoom()));
    }
}
