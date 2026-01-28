package com.github.messenger.infrastructure.repository.mapper;

import com.github.messenger.domain.entity.TextMessage;
import com.github.messenger.domain.value_objects.MessageId;
import com.github.messenger.domain.value_objects.TextContent;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.repository.entity.MessageContentJpaEntity;
import com.github.messenger.infrastructure.repository.entity.MessageJpaEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MessageMapper {
    public TextMessage toDomain(MessageJpaEntity messageJpaEntity, MessageContentJpaEntity messageContentJpaEntity) {
        return new TextMessage(
                new MessageId(messageJpaEntity.getId()),
                new UserId(messageJpaEntity.getSender().getId()),
                messageJpaEntity.getChat().getId(),
                new TextContent(messageContentJpaEntity.getText()),
                Instant.ofEpochSecond(messageJpaEntity.getSendingTime())
        );
    }
}
