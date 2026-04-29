package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.events_system.EventBroker;
import com.github.messenger.domain.events_system.event_messages.MessageSentEventMessage;
import com.github.messenger.domain.exceptions.AccessDeniedException;
import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.repository.MessageRepository;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.FileId;
import com.github.messenger.domain.value_objects.TextContent;
import com.github.messenger.domain.value_objects.UserId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class SendMessageUseCase {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final EventBroker eventBroker;
    private final SetMessagesViewedUseCase setMessagesViewedUseCase;

    public SendMessageUseCase(MessageRepository messageRepository,
                              ChatRepository chatRepository,
                              EventBroker eventBroker,
                              SetMessagesViewedUseCase setMessagesViewedUseCase) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.eventBroker = eventBroker;
        this.setMessagesViewedUseCase = setMessagesViewedUseCase;
    }

    public Message execute(
        UserId senderId,
        ChatId chatId,
        TextContent textContent,
        List<FileId> attachments,
        int userEventId) {
        if (!chatRepository.userInChat(senderId, chatId)) {
            throw new AccessDeniedException("Пользователя нет в чате");
        }

        Message message = messageRepository.saveUserMessage(
            senderId,
            chatId,
            textContent,
            attachments,
            Instant.now()
        );

        eventBroker.publishEvent(new MessageSentEventMessage(message, userEventId));
        setMessagesViewedUseCase.executeByMessage(List.of(message), chatId, senderId, userEventId);

        return message;
    }
}
