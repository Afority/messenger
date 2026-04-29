package com.github.messenger.infrastructure.websocket.controllers.response_types;

public record MessagePreviewDto(
        long senderId,
        String messageText,
        long sendingTime
) { }
