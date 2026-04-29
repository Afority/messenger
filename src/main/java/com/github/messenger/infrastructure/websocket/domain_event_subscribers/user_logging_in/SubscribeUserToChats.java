package com.github.messenger.infrastructure.websocket.domain_event_subscribers.user_logging_in;

import com.github.messenger.domain.events_system.EventSubscriber;
import com.github.messenger.domain.events_system.event_messages.UserLoggingInEventMessage;
import com.github.messenger.infrastructure.websocket.data_storage.ChatMembersData;
import org.springframework.stereotype.Component;

// регистрирует пользователя под его чаты
@Component
public class SubscribeUserToChats implements EventSubscriber<UserLoggingInEventMessage> {
    private final ChatMembersData chatMembersData;

    public SubscribeUserToChats(ChatMembersData chatMembersData) {
        this.chatMembersData = chatMembersData;
    }

    @Override
    public void execute(UserLoggingInEventMessage message) {
        System.out.println("Подписка пользователей к чатам");
        chatMembersData.subscribeUserToAllChat(message.userId());
    }

    @Override
    public Class<UserLoggingInEventMessage> getMessageClass() {
        return UserLoggingInEventMessage.class;
    }
}
