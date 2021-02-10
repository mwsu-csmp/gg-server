package edu.missouriwestern.csmp.gg.server.networking;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;

public class MqttEventForwarder implements EventListener {
    private static Logger logger = Logger.getLogger(MqttEventForwarder.class.getCanonicalName());

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void acceptEvent(Event event) {
        logger.info("propagating: " + event);
        // TODO: propagate to appropriate channel in MQTT
    }

}
