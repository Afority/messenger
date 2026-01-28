package com.github.messenger.infrastructure.websocket.controllers.server_requests;

public record PrivateChatCreated(
        String operationCode,
        Long creatorId,
        String chatId
){
    public PrivateChatCreated (Long creatorId, String chatId) {
        this("PrivateChatCreated", creatorId, chatId);
    }
}
