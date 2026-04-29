package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.ChatType;
import com.github.messenger.domain.value_objects.UserId;

import java.util.List;
import java.util.Objects;

import lombok.Getter;

public final class PersonalChat implements Chat {
    private final ChatId chatId;
    private List<UserId> participants;
    private long lastMessageNumber;

    public PersonalChat(ChatId chatId, List<UserId> participants, long lastMessageNumber) {
        Objects.requireNonNull(participants);

        if (participants.size() != 2)
            throw new IllegalStateException("В чате 1 на 1 должно быть только 2 участника");

        if (lastMessageNumber < 0)
            throw new IllegalStateException("last message number не может быть отрицательным");

        this.chatId = chatId;
        this.participants = participants;
        this.lastMessageNumber = lastMessageNumber;
    }

    @Override
    public ChatId getChatId() {
        return chatId;
    }

    @Override
    public List<UserId> getParticipants() {
        return participants;
    }

    @Override
    public long getLastMessageNumber() {
        return lastMessageNumber;
    }

    @Override
    public ChatType getType() {
        return ChatType.PERSONAL;
    }
}
