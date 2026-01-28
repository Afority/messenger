package com.github.messenger.infrastructure.websocket.controllers.response_types;

public record ChatCreatedResponse(
        String operationCode,
        String chatId,
        Long requestId,
        boolean isError,
        String errorMessage
) {
    public ChatCreatedResponse(
            String chatId,
            Long requestId,
            boolean isError,
            String errorMessage
    ) {
        this("response", chatId, requestId, isError, errorMessage);
    }
}
