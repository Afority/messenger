package com.github.messenger.domain.entity;

import com.github.messenger.domain.value_objects.ChatId;
import com.github.messenger.domain.value_objects.ChatType;
import com.github.messenger.domain.value_objects.FileId;
import com.github.messenger.domain.value_objects.UserId;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class GroupChat implements Chat {
    private final ChatId chatId;
    private List<UserId> participants;
    private String name;
    private @Nullable String description;
    private @Nullable FileId photo;
    private long lastMessageNumber;

    public GroupChat(ChatId chatId,
                     String name,
                     @Nullable String description,
                     @Nullable FileId photo,
                     List<UserId> participants,
                     long lastMessageNumber) {
        if (lastMessageNumber < 0) {
            throw new IllegalArgumentException("lastMessageNumber не может быть меньше нуля");
        }
        Objects.requireNonNull(chatId);
        Objects.requireNonNull(name);
        Objects.requireNonNull(participants);

        if (participants.isEmpty() || participants.size() < 2)
            throw new IllegalStateException("В чате должны быть хотя бы 2 участника");

        this.chatId = chatId;
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.participants = participants;
        this.lastMessageNumber = lastMessageNumber;
    }

    public String getName() {
        return name;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public @Nullable FileId getPhoto() {
        return photo;
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
        return ChatType.GROUP;
    }
}
