package com.github.messenger.infrastructure.websocket.domain_event_subscribers.chat_created;

import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.events_system.EventSubscriber;
import com.github.messenger.domain.events_system.event_messages.ChatCreatedEventMessage;
import com.github.messenger.domain.value_objects.ChatType;
import com.github.messenger.infrastructure.websocket.controllers.events.GroupChatCreatedEvent;
import com.github.messenger.infrastructure.websocket.controllers.events.PrivateChatCreatedEvent;
import com.github.messenger.infrastructure.web.dtos.UserDto;
import com.github.messenger.infrastructure.websocket.infrastructure.UsersCommunication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BroadcastChatCreated implements EventSubscriber<ChatCreatedEventMessage> {
    private final UsersCommunication usersCommunication;

    public BroadcastChatCreated(UsersCommunication usersCommunication) {
        this.usersCommunication = usersCommunication;
    }

    @Override
    public void execute(ChatCreatedEventMessage message) {
        Object event = null;

        List<UserDto> participants =
            message.participants().stream()
                .map(participant -> new UserDto(
                    participant.getId().value(),
                    participant.getUsername().getUsernameStr()))
                .toList();

        if (message.chatType() == ChatType.GROUP) {
            event = new GroupChatCreatedEvent(
                message.userEventId(),
                message.chatId().value().toString(),
                participants,
                message.chatName(),
                message.chatDescription()
            );
        }
        else if (message.chatType() == ChatType.PERSONAL) {
            event = new PrivateChatCreatedEvent(
                participants,
                message.chatId().value().toString(),
                message.userEventId()
            );
        }

        for (User user : message.participants()) {
            usersCommunication.enqueueForUserSessions(user.getId(), event);
        }
    }

    @Override
    public Class<ChatCreatedEventMessage> getMessageClass() {
        return ChatCreatedEventMessage.class;
    }
}
