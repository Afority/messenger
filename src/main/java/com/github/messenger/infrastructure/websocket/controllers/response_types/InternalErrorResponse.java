package com.github.messenger.infrastructure.websocket.controllers.response_types;

// Отправляется по вине разработчика, когда взаимодействие
public record InternalErrorResponse(
        long requestId,
        boolean isError,
        String errorMessage
) {
}
