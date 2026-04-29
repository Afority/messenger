package com.github.messenger.infrastructure.websocket.domain_event_subscribers.MessagesViewed;

import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.events_system.EventSubscriber;
import com.github.messenger.domain.events_system.event_messages.MessagesViewedEventMessage;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.MessageNumber;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.events.MessagesViewedEvent;
import com.github.messenger.infrastructure.websocket.data_storage.ChatMembersData;
import com.github.messenger.infrastructure.websocket.infrastructure.UsersCommunication;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class BroadcastMessagesViewed implements EventSubscriber<MessagesViewedEventMessage> {
    private final ChatMembersData chatMembersData;
    private final UsersCommunication usersCommunication;

    public BroadcastMessagesViewed(ChatMembersData chatMembersData,
                                   UsersCommunication usersCommunication) {
        this.chatMembersData = chatMembersData;
        this.usersCommunication = usersCommunication;
    }

    @Override
    public void execute(MessagesViewedEventMessage message) {
        Set<UserId> participants = chatMembersData.getChatParticipants(message.chatId());

        for (UserId userId : participants) {
            usersCommunication.enqueueForUserSessions(userId,
                new MessagesViewedEvent(
                    message.userEventId(),
                    message.messagesNumbers().stream()
                        .map(MessageNumber::value)
                        .toList(),
                    message.chatId().value().toString(),
                    message.viewedUser().value(),
                    message.timestamp()
                )
            );
        }
    }

    @Override
    public Class<MessagesViewedEventMessage> getMessageClass() {
        return MessagesViewedEventMessage.class;
    }
}
