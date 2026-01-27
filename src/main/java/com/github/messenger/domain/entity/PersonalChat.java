package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.ChatType;
import com.github.messenger.domain.value_objects.UserId;

import java.util.List;
import java.util.Objects;

import lombok.Getter;

@Getter
public class PersonalChat {
    private final ChatId chatId;
    private List<UserId> participants;

    public PersonalChat(ChatId chatId, List<UserId> participants) {
        Objects.requireNonNull(participants);

        if (participants.size() != 2)
            throw new IllegalStateException("В чате 1 на 1 должно быть только 2 участника");

        this.chatId = chatId;
        this.participants = participants;
    }
}
