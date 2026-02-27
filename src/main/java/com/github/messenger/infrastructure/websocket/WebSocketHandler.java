package com.github.messenger.infrastructure.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;

@Component
public class WebSocketHandler extends AbstractWebSocketHandler {
    private final HashMap<String, Long> sessions = new HashMap<>();
    private final RequestHandler requestHandler;

    public WebSocketHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Handle new connections (e.g., add session to a list for broadcasting)
        // JWT validate
        // sessions.put(session.getId(), userId)
        System.out.println("Connection established: " + session.getId());

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Handle incoming text messages
        String receivedMessage = message.getPayload();
        System.out.println("Received message: " + receivedMessage);

        requestHandler.handle(receivedMessage, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        System.out.println("Connection closed: " + session.getId() + " status: " + status);
    }
}

