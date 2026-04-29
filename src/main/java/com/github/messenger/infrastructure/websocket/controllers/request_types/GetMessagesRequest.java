package com.github.messenger.infrastructure.websocket.controllers.request_types;

public record GetMessagesRequest (
        String chatId,
        long start,
        long limit
) {
}
