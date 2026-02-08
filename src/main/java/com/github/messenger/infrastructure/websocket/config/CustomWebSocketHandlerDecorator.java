package com.github.messenger.infrastructure.websocket.config;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.security.Principal;

public class CustomWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

    public CustomWebSocketHandlerDecorator(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {

        Object userId = session.getAttributes().get("userId");

        if (userId != null) {
            session = new WebSocketSessionWrapper(session) {
                @Override
                public Principal getPrincipal() {
                    return new StompPrincipal(userId.toString());
                }
            };
        }

        super.afterConnectionEstablished(session);
    }
}

