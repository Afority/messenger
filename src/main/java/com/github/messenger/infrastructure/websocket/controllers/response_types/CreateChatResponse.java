package com.github.messenger.infrastructure.websocket.controllers.response_types;

public record CreateChatResponse(
        String chatId,
        Long requestId,
        boolean isError,
        String errorMessage
) {
}
