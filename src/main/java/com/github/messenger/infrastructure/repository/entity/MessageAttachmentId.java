package com.github.messenger.infrastructure.repository.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class MessageAttachmentId implements Serializable {
    private Long messageId;
    private UUID fileId;

    public MessageAttachmentId() {
    }

    public MessageAttachmentId(Long messageId, UUID fileId) {
        this.messageId = messageId;
        this.fileId = fileId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageAttachmentId that = (MessageAttachmentId) o;
        return Objects.equals(messageId, that.messageId) && Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, fileId);
    }
}
