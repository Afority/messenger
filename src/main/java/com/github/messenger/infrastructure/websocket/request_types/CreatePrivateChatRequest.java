package com.github.messenger.infrastructure.websocket.request_types;

public record CreatePrivateChatRequest(
        Long userId,
        Long requestId
) {
}
