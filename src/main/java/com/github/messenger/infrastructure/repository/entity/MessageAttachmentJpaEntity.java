package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "messages_attachments")
public class MessageAttachmentJpaEntity {
    @EmbeddedId
    private MessageAttachmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    private MessageJpaEntity message;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fileId")
    @JoinColumn(name = "file_id")
    private FileJpaEntity file;

    public MessageAttachmentJpaEntity() {}

    public MessageAttachmentJpaEntity(MessageAttachmentId id, MessageJpaEntity message, FileJpaEntity file) {
        this.id = id;
        this.message = message;
        this.file = file;
    }
}
