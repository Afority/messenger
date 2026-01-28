package com.github.messenger.infrastructure.websocket.controllers.request_types;

public record CreatePrivateChatRequest(
        Long userId,
        Long requestId
) {
}
