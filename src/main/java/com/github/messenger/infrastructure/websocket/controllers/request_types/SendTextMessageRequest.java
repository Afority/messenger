package com.github.messenger.infrastructure.websocket.controllers.request_types;

public record SendTextMessageRequest(
        long requestId,
        String chatId,
        String message
) {
}
