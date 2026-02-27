package com.github.messenger.infrastructure.websocket.request_types;

public record SendTextMessageRequest(
        long requestId,
        String chatId,
        String message
) {
}
