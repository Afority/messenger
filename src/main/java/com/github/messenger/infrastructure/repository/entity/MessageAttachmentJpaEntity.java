package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "message_attachment")
public class MessageAttachmentJpaEntity {
    @EmbeddedId
    private MessageAttachmentId id;

    @ManyToOne
    @MapsId("messageId")
    @JoinColumn(name = "message_content_id")
    private MessageContentJpaEntity content;

    @ManyToOne
    @MapsId("fileId")
    @JoinColumn(name = "file_id")
    private FileJpaEntity file;
}
