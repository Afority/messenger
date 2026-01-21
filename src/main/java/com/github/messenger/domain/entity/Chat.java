package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.UserId;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Chat {
    private final UUID chatId;
    private List<UserId> participants;

    public Chat(UUID chatId, List<UserId> participants) {
        Objects.requireNonNull(chatId, "chatId cannot be null");
        Objects.requireNonNull(participants, "participants cannot be null");

        this.chatId = chatId;
        this.participants = participants;
    }
}
