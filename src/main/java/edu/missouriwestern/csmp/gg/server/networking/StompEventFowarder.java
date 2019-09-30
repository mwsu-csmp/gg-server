package edu.missouriwestern.csmp.gg.server.networking;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;

public class StompEventFowarder implements EventListener {
    private static Logger logger = Logger.getLogger(StompEventFowarder.class.getCanonicalName());

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void acceptEvent(Event event) {
        logger.info("propagating: " + event);
        messagingTemplate.convertAndSend("/topic/event", event.toString());
    }

}
