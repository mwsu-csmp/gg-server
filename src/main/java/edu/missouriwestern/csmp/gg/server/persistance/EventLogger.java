package edu.missouriwestern.csmp.gg.server.persistance;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;

import java.util.logging.Logger;

public class EventLogger implements EventListener {

    private static Logger logger = Logger.getLogger(EventLogger.class.getCanonicalName());

    @Override
    public void acceptEvent(Event event) {
        logger.info(event.toString());
    }
}
