package com.github.messenger.infrastructure.websocket.domain_event_subscribers.message_sent;

import com.github.messenger.domain.entity.Attachment;
import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.entity.TextMessage;
import com.github.messenger.domain.events_system.EventSubscriber;
import com.github.messenger.domain.events_system.event_messages.MessageSentEventMessage;
import com.github.messenger.domain.value_objects.TextContent;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.controllers.events.SentMessageEvent;
import com.github.messenger.infrastructure.websocket.data_storage.ChatMembersData;
import com.github.messenger.infrastructure.websocket.infrastructure.UsersCommunication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class BroadcastMessageToChat implements EventSubscriber<MessageSentEventMessage> {
    private final UsersCommunication usersCommunication;
    private final ChatMembersData chatMembersData;

    public BroadcastMessageToChat(UsersCommunication usersCommunication,
                                  ChatMembersData chatMembersData) {
        this.usersCommunication = usersCommunication;
        this.chatMembersData = chatMembersData;
    }

    @Override
    public void execute(MessageSentEventMessage eventMessage) {
        // рассылка всем пользователям, состоящих в чате
        Message message = eventMessage.message();

        TextContent textContent = null;
        List<Attachment> attachments = null;

        if (message instanceof TextMessage textMessage) {
            textContent = textMessage.getContent();
            attachments = textMessage.getAttachments();
        }

        SentMessageEvent sentMessageEvent = new SentMessageEvent(
            eventMessage.userEventId(),
            message.sender().value(),
            message.chat().value().toString(),
            textContent == null ? null : textContent.getText(),
            attachments,
            message.sentAt().getEpochSecond(),
            message.number().value()
        );

        Set<UserId> chatParticipants = chatMembersData.getChatParticipants(message.chat());

        for (UserId userId : chatParticipants) {
            usersCommunication.enqueueForUserSessions(userId, sentMessageEvent);
        }
    }

    @Override
    public Class<MessageSentEventMessage> getMessageClass() {
        return MessageSentEventMessage.class;
    }
}
