package com.github.messenger.infrastructure.repository.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class MessageViewIdJpaEntity implements Serializable {
    private Long userId;
    private Long messageId;

    public MessageViewIdJpaEntity(Long userId, Long messageId) {
        this.userId = userId;
        this.messageId = messageId;
    }

    public MessageViewIdJpaEntity() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageViewIdJpaEntity that = (MessageViewIdJpaEntity) o;
        return Objects.equals(userId, that.userId) && Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, messageId);
    }
}
