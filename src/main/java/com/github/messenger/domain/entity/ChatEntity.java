package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.ChatId;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class ChatEntity {
    private ChatId chatId;
    private List<UserEntity> participants;

    public ChatEntity(ChatId chatId, List<UserEntity> participants) {
        Objects.requireNonNull(chatId, "chatId cannot be null");
        Objects.requireNonNull(participants, "participants cannot be null");

        this.chatId = chatId;
        this.participants = participants;
    }
}
