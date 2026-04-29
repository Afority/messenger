package com.github.messenger.infrastructure.websocket.controllers.server_requests;

import java.util.List;

public record MessageArrived(
        String operationCode,
        String chatId, // UUID
        String text,
        long timestamp
) {
    public MessageArrived (String chatId,
                           String text,
                           long timestamp) {
        this("MessageArrived", chatId, text, timestamp);
    }
}
