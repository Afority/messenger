package com.github.messenger.infrastructure.websocket.domain_event_subscribers.chat_created;

import com.github.messenger.domain.entity.User;
import com.github.messenger.domain.events_system.EventSubscriber;
import com.github.messenger.domain.events_system.event_messages.ChatCreatedEventMessage;
import com.github.messenger.infrastructure.websocket.data_storage.ChatMembersData;
import org.springframework.stereotype.Component;

@Component
public class RegisterUsersToGroupChats implements EventSubscriber<ChatCreatedEventMessage> {
    private final ChatMembersData chatMembersData;

    public RegisterUsersToGroupChats(ChatMembersData chatMembersData) {
        this.chatMembersData = chatMembersData;
    }

    @Override
    public void execute(ChatCreatedEventMessage message) {
        for (User participant : message.participants()) {
            chatMembersData.subscribeToChat(message.chatId(), participant.getId());
        }
    }

    @Override
    public Class<ChatCreatedEventMessage> getMessageClass() {
        return ChatCreatedEventMessage.class;
    }
}
