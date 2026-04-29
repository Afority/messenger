package com.github.messenger.infrastructure.websocket.controllers.server_requests;

public record PrivateChatCreated(
        long withUser,
        String chatId,
        String eventId
){ }
