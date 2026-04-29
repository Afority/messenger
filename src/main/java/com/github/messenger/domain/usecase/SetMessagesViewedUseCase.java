package com.github.messenger.domain.usecase;

import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.events_system.EventBroker;
import com.github.messenger.domain.events_system.event_messages.MessagesViewedEventMessage;
import com.github.messenger.domain.exceptions.ChatNotFoundException;
import com.github.messenger.domain.repository.ChatRepository;
import com.github.messenger.domain.repository.MessageRepository;
import com.github.messenger.domain.repository.MessageViewRepository;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.MessageNumber;
import com.github.messenger.domain.value_objects.UserId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class SetMessagesViewedUseCase {
    private final MessageViewRepository messageViewRepository;
    private final ChatRepository chatRepository;
    private final EventBroker eventBroker;
    private final MessageRepository messageRepository;

    public SetMessagesViewedUseCase(MessageViewRepository messageViewRepository,
                                    ChatRepository chatRepository,
                                    EventBroker eventBroker,
                                    MessageRepository messageRepository) {
        this.messageViewRepository = messageViewRepository;
        this.chatRepository = chatRepository;
        this.eventBroker = eventBroker;
        this.messageRepository = messageRepository;
    }

    public void executeByMessage(List<Message> messages, ChatId chatId, UserId userId, int userEventId) {
        if (!chatRepository.userInChat(userId, chatId)) {
            throw new ChatNotFoundException();
        }

        messageViewRepository.setMark(messages.stream().map(Message::id).toList(), userId);

        eventBroker.publishEvent(
            new MessagesViewedEventMessage(
                messages.stream().map(Message::id).toList(),
                messages.stream().map(Message::number).toList(),
                chatId,
                userId,
                Instant.now().getEpochSecond(),
                userEventId
            )
        );
    }

    public void executeByMessageNumber(List<MessageNumber> messagesNumbers, ChatId chatId, UserId userId, int userEventId) {
        if (!chatRepository.userInChat(userId, chatId)) {
            throw new ChatNotFoundException();
        }

        List<Message> messages = messageRepository.getMessages(messagesNumbers, chatId);

        executeByMessage(messages, chatId, userId, userEventId);
    }
}
