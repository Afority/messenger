package com.github.messenger.domain.entity;

import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ChatEntity {
    private final UUID chatId;
    private List<UserEntity> participants;

    public ChatEntity(UUID chatId, List<UserEntity> participants) {
        Objects.requireNonNull(chatId, "chatId cannot be null");
        Objects.requireNonNull(participants, "participants cannot be null");

        this.chatId = chatId;
        this.participants = participants;
    }
}
