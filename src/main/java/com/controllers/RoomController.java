package com.controllers;

import com.rooms.Sandbox;
import edu.missouriwestern.csmp.gg.base.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Controller
public class RoomController {

    Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private Game game;

    @GetMapping("/board/{boardId}")
    @ResponseBody
    public String getBoard(@PathVariable String boardId) {
        log.info("reading " + boardId);
        var board = game.getBoard(boardId);
        return board.toString();
    }

    @MessageMapping(value = "/com/board")
    @SendTo("/topic/room")
    public Message createRoom(Sandbox room) throws Exception{

        log.info("sending tile locations to client");
        return new Message(HtmlUtils.htmlEscape(room.getRoom()));
    }
}
