package com.controllers;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;

import java.util.logging.Logger;

public class EventLogger implements EventListener {

    private static Logger logger = Logger.getLogger(GameMapping.class.getCanonicalName());

    @Override
    public void accept(Event event) {
        logger.info(event.toString());
    }
}
