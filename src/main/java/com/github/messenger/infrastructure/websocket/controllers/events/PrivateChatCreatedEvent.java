package com.github.messenger.infrastructure.websocket.controllers.events;

import com.github.messenger.infrastructure.web.dtos.UserDto;

import java.util.List;

public class PrivateChatCreatedEvent extends BasicEventMessage {
    private record Payload(
        List<UserDto> users,
        String chatId,
        int eventId
    ) { }

    public PrivateChatCreatedEvent(List<UserDto> users, String chatId, int userEventId) {
        super(userEventId, 1, new Payload(users, chatId, userEventId));
    }
}
