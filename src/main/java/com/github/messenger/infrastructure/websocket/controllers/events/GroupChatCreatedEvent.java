package com.github.messenger.infrastructure.websocket.controllers.events;

import com.github.messenger.infrastructure.web.dtos.UserDto;
import java.util.List;

public class GroupChatCreatedEvent extends BasicEventMessage {
    private record Payload(
            String chatId,
            List<UserDto> participants,
            String name,
            String description
    ) {}

    public GroupChatCreatedEvent(int eventId,
                                 String chatId, List<UserDto> participants, String name, String description) {
        super(eventId, 2, new Payload(chatId, participants, name, description));
    }
}
