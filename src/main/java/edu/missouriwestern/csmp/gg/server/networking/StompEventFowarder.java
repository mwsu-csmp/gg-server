package edu.missouriwestern.csmp.gg.server.networking;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

public class StompEventFowarder implements EventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void acceptEvent(Event event) {
        messagingTemplate.convertAndSend("/topic/event", event.toString());
    }

    public Consumer<EventListener> registerEventReceiver() {
        return listener -> {
            // TODO: register listener to receive events from STOMP server
        };
    }
}
