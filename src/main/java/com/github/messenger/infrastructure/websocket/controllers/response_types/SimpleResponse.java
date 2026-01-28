package com.github.messenger.infrastructure.websocket.controllers.response_types;

public record SimpleResponse(
        String operationCode,
        Long requestId,
        boolean isError,
        String errorMessage
) {
    public SimpleResponse(
            Long requestId,
            boolean isError,
            String errorMessage
    ) {
        this("response", requestId, isError, errorMessage);
    }
}
