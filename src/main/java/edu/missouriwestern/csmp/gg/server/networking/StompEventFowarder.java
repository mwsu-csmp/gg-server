package edu.missouriwestern.csmp.gg.server.networking;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.server.game.MissouriWizardStateUniversityGame;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

@Component("stomp-event-publisher")
public class StompEventFowarder implements EventListener {

    private static Logger logger = Logger.getLogger(MissouriWizardStateUniversityGame.class.getCanonicalName());

    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    @Override
    public void accept(Event event) {

        logger.info("Test parse: ");
        messagingTemplate.convertAndSend("/topic/event", event.toString());
    }
}
