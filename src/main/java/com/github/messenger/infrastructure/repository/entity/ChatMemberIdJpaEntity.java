package com.github.messenger.infrastructure.repository.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ChatMemberIdJpaEntity implements Serializable {
    private Long userId;
    private UUID chatId;

    public ChatMemberIdJpaEntity() {
    }

    public ChatMemberIdJpaEntity(Long userId, UUID chatId) {
        this.userId = userId;
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChatMemberIdJpaEntity that = (ChatMemberIdJpaEntity) o;
        return Objects.equals(userId, that.userId) && Objects.equals(chatId, that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, chatId);
    }
}
