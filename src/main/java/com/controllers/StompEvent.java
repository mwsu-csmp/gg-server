package com.controllers;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

@Component("stomp-event-publisher")
public class StompEvent implements EventListener {

    private static Logger logger = Logger.getLogger(GameMapping.class.getCanonicalName());

    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    @Override
    public void accept(Event event) {

        logger.info("Test parse: ");
        messagingTemplate.convertAndSend("/topic/event", event.toString());
    }
}
