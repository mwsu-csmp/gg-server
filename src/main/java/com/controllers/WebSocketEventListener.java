package com.controllers;

import com.player.StompClient;
import edu.missouriwestern.csmp.gg.base.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

// adapted from example at: https://www.callicoder.com/spring-boot-websocket-chat-example/

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private Game game;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("player connected: " + event.getUser().getName());
        StompClient stompClient = new StompClient(event.getUser().getName(), game);
        game.addPlayer(stompClient);
        GameMapping.save(stompClient);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            logger.info("User Disconnected : " + username);

            //ChatMessage chatMessage = new ChatMessage();
            //chatMessage.setType(ChatMessage.MessageType.LEAVE);
            //chatMessage.setSender(username);

            //messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
