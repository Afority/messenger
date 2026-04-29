package com.github.messenger.infrastructure.websocket.controllers.request_types;

import java.util.List;

public record MessagesViewedRequest (List<Long> messagesNumbers, String chatId, int userEventId) {
}
