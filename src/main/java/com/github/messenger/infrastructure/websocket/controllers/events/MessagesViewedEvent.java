package com.github.messenger.infrastructure.websocket.controllers.events;

import java.util.List;

public class MessagesViewedEvent extends BasicEventMessage {
    private record Payload(
        List<Long> messagesIds,
        String chatId,
        Long userId,
        long timestamp
    ) { }

    public MessagesViewedEvent(int eventId, List<Long> messagesIds, String chatId, Long userId, long timestamp) {
        super(eventId, 4, new Payload(messagesIds, chatId, userId, timestamp));
    }
}
