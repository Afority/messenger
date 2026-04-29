package com.github.messenger.infrastructure.websocket.domain_event_subscribers.user_logged_out;

import com.github.messenger.domain.events_system.EventSubscriber;
import com.github.messenger.domain.events_system.event_messages.UserLoggedOutEventMessage;
import com.github.messenger.infrastructure.websocket.data_storage.ChatMembersData;
import org.springframework.stereotype.Component;

@Component
public class UnsubscribeUserFromChats implements EventSubscriber<UserLoggedOutEventMessage> {
    private final ChatMembersData chatMembersData;

    public UnsubscribeUserFromChats(ChatMembersData chatMembersData) {
        this.chatMembersData = chatMembersData;
    }

    @Override
    public void execute(UserLoggedOutEventMessage message) {
        chatMembersData.unsubscribeUser(message.userId());
    }

    @Override
    public Class<UserLoggedOutEventMessage> getMessageClass() {
        return UserLoggedOutEventMessage.class;
    }
}
