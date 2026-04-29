package com.github.messenger.infrastructure.repository.mapper;

import com.github.messenger.domain.entity.Attachment;
import com.github.messenger.domain.entity.Message;
import com.github.messenger.domain.entity.ServiceMessage;
import com.github.messenger.domain.entity.TextMessage;
import com.github.messenger.domain.exceptions.MessageTypeIsWrong;
import com.github.messenger.domain.value_objects.*;
import com.github.messenger.infrastructure.repository.entity.ContentType;
import com.github.messenger.infrastructure.repository.entity.FileJpaEntity;
import com.github.messenger.infrastructure.repository.entity.MessageAttachmentJpaEntity;
import com.github.messenger.infrastructure.repository.entity.MessageJpaEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class MessageMapper {
    public Message toDomain(MessageJpaEntity messageJpaEntity, List<MessageAttachmentJpaEntity> attachments) {
        if (messageJpaEntity.getContentType() == ContentType.TEXT) {
            return new TextMessage(
                new MessageId(messageJpaEntity.getId()),
                new MessageNumber(messageJpaEntity.getMessageNumber()),
                new UserId(messageJpaEntity.getSender().getId()),
                new ChatId(messageJpaEntity.getChat().getId()),
                new TextContent(messageJpaEntity.getText()),
                attachments.stream()
                    .map(MessageAttachmentJpaEntity::getFile)
                    .map(attachment -> new Attachment(
                        new FileId(
                            attachment.getId()),
                            attachment.getFilename(),
                            attachment.getMimeType(),
                            attachment.getSize()
                        )
                    )
                    .toList(),
                Instant.ofEpochSecond(messageJpaEntity.getSendingTime())
            );
        }
//        if (messageJpaEntity.getContentType() == ContentType.SERVICE) {
//            return new ServiceMessage(
//                new MessageId(messageJpaEntity.getId()),
//                new MessageNumber(messageJpaEntity.getMessageNumber()),
//                new UserId(messageJpaEntity.getSender().getId()),
//                new ChatId(messageJpaEntity.getChat().getId()),
//            );
//        }
        throw new MessageTypeIsWrong("В разработке");
    }
}
