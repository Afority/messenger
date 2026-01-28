package com.github.messenger.infrastructure.repository.adapter;

import com.github.messenger.domain.entity.TextMessage;
import com.github.messenger.domain.repository.MessageRepository;
import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.TextContent;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.repository.MessageContentRepository;
import com.github.messenger.infrastructure.repository.MessageJpaRepository;
import com.github.messenger.infrastructure.repository.entity.*;
import com.github.messenger.infrastructure.repository.mapper.MessageMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class MessageRepositoryAdapter implements MessageRepository {
    MessageJpaRepository messageRepository;
    MessageContentRepository messageContentRepository;
    MessageMapper messageMapper;

    public MessageRepositoryAdapter(
            MessageJpaRepository messageRepository,
            MessageContentRepository messageContentRepository,
            MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageContentRepository = messageContentRepository;
        this.messageMapper = messageMapper;
    }

    @Override
    @Transactional
    public TextMessage save(UserId senderId, ChatId chatId, TextContent textContent, Instant sendingTime) {
        long lastMessageNumber = messageRepository.findLastMessageNumber(chatId.value().toString());

        MessageJpaEntity message = new MessageJpaEntity(
                lastMessageNumber,
                new UserJpaEntity(senderId.value()),
                new ChatJpaEntity(chatId.value()),
                sendingTime.getEpochSecond(),
                ContentType.TEXT
        );

        MessageJpaEntity savedMessage = messageRepository.save(message);
        MessageContentJpaEntity savedMessageContent =
                messageContentRepository.save(new MessageContentJpaEntity(savedMessage, textContent.getText()));

        return messageMapper.toDomain(savedMessage, savedMessageContent);
    }
}
